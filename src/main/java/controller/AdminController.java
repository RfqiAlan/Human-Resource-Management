package controller;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.CutiItem;
import util.DataBaseHelper;
import java.text.NumberFormat;
import java.util.Locale;
import java.io.IOException;
import java.sql.*;
import model.CutiDisetujuiRecord;
import model.AbsensiRecord;

public class AdminController {
    public Button btnHapusCutiDisetujui;
    @FXML private TextField newUsername;
    @FXML private PasswordField newPassword;
    @FXML private TextField newGaji;
    @FXML private Button btnDaftar;
    @FXML private ListView<CutiItem> listCuti;
    @FXML private TableView<AbsensiRecord> tableAbsensiHariIni;
    @FXML private TableColumn<AbsensiRecord, String> colUsername;
    @FXML private TableColumn<AbsensiRecord, String> colTanggal;
    @FXML private TableColumn<AbsensiRecord, String> colJam;
    @FXML private TableColumn<AbsensiRecord, String> colKeterangan;
    @FXML private TableView<CutiDisetujuiRecord> tableCutiDisetujui;
    @FXML private TableColumn<CutiDisetujuiRecord, String> colCutiUser;
    @FXML private TableColumn<CutiDisetujuiRecord, String> colCutiAlasan;
    @FXML private StackPane marqueePane;
    @FXML private Label marqueeLabel;
    private TranslateTransition marqueeTransition;

    @FXML
    private void initialize() {
        if (colUsername != null)  colUsername.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        if (colTanggal != null)   colTanggal.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTanggal()));
        if (colJam != null)       colJam.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getJam()));
        if (colKeterangan != null)colKeterangan.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKeterangan()));

        if (colCutiUser != null) colCutiUser.setCellValueFactory(data -> data.getValue().usernameProperty());
        if (colCutiAlasan != null) colCutiAlasan.setCellValueFactory(data -> data.getValue().alasanProperty());

        loadCuti();
        loadApprovedLeave();
        setupMarquee("Selamat datang di Dashboard Admin. Kelola data karyawan, absensi, dan cuti dengan mudah dan cepat!");
    }

    // Utility for pop-up error/info alerts
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // MARQUEE / running text
    public void setupMarquee(String text) {
        if (marqueeLabel == null || marqueePane == null) return;
        marqueeLabel.setText(text);
        marqueeLabel.applyCss();
        marqueeLabel.layout();

        double sceneWidth = marqueePane.getWidth() > 0 ? marqueePane.getWidth() : 1180; // fallback
        double labelWidth = marqueeLabel.getWidth();

        marqueeLabel.setLayoutX(sceneWidth);

        // Reset dan buat animasi
        if (marqueeTransition != null) marqueeTransition.stop();
        marqueeTransition = new TranslateTransition(Duration.seconds(10), marqueeLabel);
        marqueeTransition.setFromX(sceneWidth);
        marqueeTransition.setToX(-labelWidth);
        marqueeTransition.setCycleCount(Animation.INDEFINITE);
        marqueeTransition.setInterpolator(Interpolator.LINEAR);
        marqueeTransition.play();
    }



    @FXML
    private void handleDaftar() {
        String username = newUsername.getText();
        String password = newPassword.getText();
        String gajiStr = newGaji.getText();

        if (username.isEmpty() || password.isEmpty() || gajiStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Lengkap", "Semua kolom harus diisi.");
            return;
        }

        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        if (!password.matches(passwordPattern)) {
            showAlert(Alert.AlertType.ERROR, "Password Tidak Valid", "Password minimal 8 karakter, mengandung huruf besar, huruf kecil, dan angka.");
            return;
        }

        try {
            double gaji = Double.parseDouble(gajiStr);

            // Format gaji dengan koma ribuan (misal: 1.000)
            NumberFormat formatter = NumberFormat.getInstance(new Locale("id", "ID"));
            String gajiFormatted = formatter.format(gaji);

            try (Connection conn = DataBaseHelper.connect()) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (username, password, role, gaji_per_hari) VALUES (?, ?, 'karyawan', ?)");
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setDouble(3, gaji);
                ps.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Berhasil",
                        "Berhasil mendaftarkan karyawan: " + username + "\nGaji per hari: Rp " + gajiFormatted);
                newUsername.clear();
                newPassword.clear();
                newGaji.clear();
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed: users.username")) {
                showAlert(Alert.AlertType.ERROR, "Username Terdaftar", "Username sudah terdaftar, silakan pilih username lain.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal Daftar", "Gagal mendaftarkan: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Gaji harus berupa angka.");
        }
    }

    private void loadCuti() {
        listCuti.getItems().clear();
        try (Connection conn = DataBaseHelper.connect()) {
            String query = """
            SELECT c.user_id, u.username, c.alasan, c.status
            FROM cuti c
            JOIN users u ON c.user_id = u.user_id
            WHERE c.status = 'proses'
        """;
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String alasan = rs.getString("alasan");
                String status = rs.getString("status");

                CutiItem item = new CutiItem(userId, username, alasan, status);
                listCuti.getItems().add(item);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data cuti: " + e.getMessage());
        }
    }

    private void loadApprovedLeave() {
        ObservableList<CutiDisetujuiRecord> data = FXCollections.observableArrayList();
        try (Connection conn = DataBaseHelper.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT c.cuti_id, u.username, c.alasan FROM cuti c JOIN users u ON c.user_id = u.user_id WHERE c.status = 'disetujui'");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.add(new CutiDisetujuiRecord(
                        rs.getInt("cuti_id"),
                        rs.getString("username"),
                        rs.getString("alasan")
                ));
            }
            if (tableCutiDisetujui != null) tableCutiDisetujui.setItems(data);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat cuti disetujui: " + e.getMessage());
        }
    }

    @FXML
    private void setujuiCuti() {
        prosesCuti("disetujui");
    }

    @FXML
    private void tolakCuti() {
        prosesCuti("ditolak");
    }

    private void prosesCuti(String statusBaru) {
        CutiItem selected = listCuti.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Tidak Ada Pilihan", "Pilih permintaan cuti terlebih dahulu.");
            return;
        }

        try (Connection conn = DataBaseHelper.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE cuti SET status = ? WHERE user_id = ? AND alasan = ? AND status = 'proses'"
            );
            ps.setString(1, statusBaru);
            ps.setInt(2, selected.getUserId());
            ps.setString(3, selected.getAlasan());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Cuti berhasil diperbarui.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Cuti gagal diperbarui.");
            }

            loadCuti();
            loadApprovedLeave();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memperbarui cuti: " + e.getMessage());
        }
    }

    @FXML
    private void handleHapusCutiDisetujui() {
        CutiDisetujuiRecord selected = tableCutiDisetujui.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Tidak Ada Pilihan", "Pilih cuti disetujui yang ingin dihapus.");
            return;
        }
        int cutiId = selected.getCutiId();
        try (Connection conn = DataBaseHelper.connect()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM cuti WHERE cuti_id = ?");
            ps.setInt(1, cutiId);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Cuti disetujui berhasil dihapus.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus cuti disetujui.");
            }
            loadApprovedLeave();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus cuti disetujui: " + e.getMessage());
        }
    }



    @FXML
    private void handleLihatAbsensiHariIni() {
        ObservableList<AbsensiRecord> data = FXCollections.observableArrayList();

        try (Connection conn = DataBaseHelper.connect()) {
            String sql = """
            SELECT u.username, a.tanggal
            FROM absensi a
            JOIN users u ON a.user_id = u.user_id
            ORDER BY a.tanggal DESC
        """;
            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                String tanggalStr = rs.getString("tanggal"); // yyyy-MM-dd HH:mm:ss

                String tanggal = "--";
                String jam = "--:--:--";
                String keterangan = "";
                if (tanggalStr != null && tanggalStr.length() >= 19) {
                    tanggal = tanggalStr.substring(0, 10);
                    jam = tanggalStr.substring(11, 19);
                    if (jam.compareTo("10:00:00") > 0) {
                        keterangan = "Terlambat";
                    }
                }
                data.add(new AbsensiRecord(username, tanggal, jam, keterangan));
            }

            tableAbsensiHariIni.setItems(data);
        } catch (SQLException e) {
            tableAbsensiHariIni.setItems(FXCollections.observableArrayList());
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat absensi: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) btnDaftar.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setMaximized(true);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Logout Error", "Gagal logout: " + e.getMessage());
        }
    }
}