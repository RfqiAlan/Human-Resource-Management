package controller;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
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
import model.AbsensiRecord;
import util.DataBaseHelper;
import java.text.NumberFormat;
import java.util.Locale;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import model.Karyawan;
public class KaryawanController {

    public Button btnAjukanCuti;
    public Button btnLihatGaji;
    @FXML private Label labelStatus;
    @FXML private Button btnAbsensi;
    @FXML private TextArea fieldCuti;

    @FXML private TextArea areaCutiStatus;

    @FXML private Label labelGaji;

    @FXML private StackPane marqueePane;
    @FXML private Label marqueeLabel;
    // Tambahan untuk riwayat absensi
    @FXML private TableView<AbsensiRecord> tableRiwayatAbsensi;
    @FXML private TableColumn<AbsensiRecord, String> colTanggal;
    @FXML private TableColumn<AbsensiRecord, String> colJam;
    @FXML private TableColumn<AbsensiRecord, String> colKeterangan;
    private TranslateTransition marqueeTransition;
    private String username = "";
    private int userId;

    @FXML
    public void initialize() {
        username = LoginControllerHelper.getLoggedInUsername();
        userId = getUserIdByUsername(username);

        // Inisialisasi kolom TableView riwayat absensi
        if (colTanggal != null) colTanggal.setCellValueFactory(data -> data.getValue().tanggalProperty());
        if (colJam != null) colJam.setCellValueFactory(data -> data.getValue().jamProperty());
        if (colKeterangan != null) colKeterangan.setCellValueFactory(data -> data.getValue().keteranganProperty());

        loadRiwayatAbsensiSaya();
        setupMarquee("Selamat datang di Dashboard Karyawan! Jangan lupa absen dan cek status cuti Anda.");
    }
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
    private int getUserIdByUsername(String username) {
        try (Connection conn = DataBaseHelper.connect();
             PreparedStatement ps = conn.prepareStatement("SELECT user_id FROM users WHERE username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (Exception e) {
            System.out.println("Gagal ambil user_id: " + e.getMessage());
        }
        return -1;
    }

    @FXML
    private void handleAbsensi() {
        // Gunakan zona waktu UTC+8
        ZoneId zoneId = ZoneId.of("Asia/Makassar");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        String tanggal = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String tanggalHariIni = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try (Connection conn = DataBaseHelper.connect()) {
            // 1. Cek apakah sudah absen hari ini
            PreparedStatement cekPs = conn.prepareStatement(
                    "SELECT COUNT(*) FROM absensi WHERE user_id = ? AND tanggal LIKE ?"
            );
            cekPs.setInt(1, userId);
            cekPs.setString(2, tanggalHariIni + "%"); // LIKE '2025-06-13%'
            ResultSet rs = cekPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                labelStatus.setText("Anda sudah absen hari ini.");
                return;
            }

            // 2. Jika belum, lakukan insert
            PreparedStatement ps = conn.prepareStatement("INSERT INTO absensi (user_id, tanggal) VALUES (?, ?)");
            ps.setInt(1, userId);
            ps.setString(2, tanggal);
            ps.executeUpdate();
            labelStatus.setText("Absensi berhasil untuk " + username);
            loadRiwayatAbsensiSaya(); // refresh riwayat setelah absensi
        } catch (Exception e) {
            labelStatus.setText("Gagal absensi: " + e.getMessage());
        }
    }
    @FXML
    private void handleAjukanCuti() {
        String alasan = fieldCuti.getText();
        if (alasan.isEmpty()) {
            areaCutiStatus.setText("Alasan cuti tidak boleh kosong.");
            return;
        }

        try (Connection conn = DataBaseHelper.connect();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO cuti (user_id, alasan, status) VALUES (?, ?, 'proses')")) {
            ps.setInt(1, userId);
            ps.setString(2, alasan);
            ps.executeUpdate();
            areaCutiStatus.setText("Cuti diajukan: " + alasan);
            fieldCuti.clear();
        } catch (Exception e) {
            areaCutiStatus.setText("Gagal ajukan cuti: " + e.getMessage());
        }
    }

    @FXML
    private void handleLihatCuti() {
        try (Connection conn = DataBaseHelper.connect();
             PreparedStatement ps = conn.prepareStatement("SELECT alasan, status FROM cuti WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            StringBuilder result = new StringBuilder();
            while (rs.next()) {
                result.append("Alasan: ").append(rs.getString("alasan"))
                        .append(" | Status: ").append(rs.getString("status"))
                        .append("\n");
            }
            areaCutiStatus.setText(result.toString());
        } catch (Exception e) {
            areaCutiStatus.setText("Gagal lihat cuti: " + e.getMessage());
        }
    }


    @FXML
    private void handleLihatGaji() {
        try (Connection conn = DataBaseHelper.connect()) {
            // Hitung jumlah hari unik absensi
            PreparedStatement ps1 = conn.prepareStatement("SELECT COUNT(DISTINCT tanggal) AS jumlahHari FROM absensi WHERE user_id = ?");
            ps1.setInt(1, userId);
            ResultSet rs1 = ps1.executeQuery();
            int jumlahHari = rs1.next() ? rs1.getInt("jumlahHari") : 0;

            // Ambil data user (lengkap)
            PreparedStatement ps2 = conn.prepareStatement("SELECT user_id, username, password, gaji_per_hari FROM users WHERE user_id = ?");
            ps2.setInt(1, userId);
            ResultSet rs2 = ps2.executeQuery();

            if (rs2.next()) {
                int id = rs2.getInt("user_id");
                String uname = rs2.getString("username");
                String pwd = rs2.getString("password");
                double gajiPerHari = rs2.getDouble("gaji_per_hari");

                // Buat objek Karyawan
                Karyawan k = new Karyawan(id, uname, pwd, gajiPerHari);

                double total = jumlahHari * k.getGajiPerHari();

                // Format angka ribuan
                NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
                String gajiPerHariStr = nf.format(k.getGajiPerHari());
                String totalStr = nf.format(total);

                // Gunakan getInfo() untuk info summary, atau buat custom string
                String info = String.format("Gaji: Rp%s", gajiPerHariStr);

                labelGaji.setText(info + " | Hari kerja: " + jumlahHari + " | Total gaji: Rp" + totalStr);
            } else {
                labelGaji.setText("Data karyawan tidak ditemukan.");
            }
        } catch (Exception e) {
            labelGaji.setText("Gagal hitung gaji: " + e.getMessage());
        }
    }

    // Tambahan: Method untuk load riwayat absensi karyawan sendiri
    private void loadRiwayatAbsensiSaya() {
        if (tableRiwayatAbsensi == null) return; // Jika TableView tidak ada di scene
        ObservableList<AbsensiRecord> data = FXCollections.observableArrayList();

        try (Connection conn = DataBaseHelper.connect()) {
            String sql = """
                SELECT tanggal
                FROM absensi
                WHERE user_id = ?
                ORDER BY tanggal DESC
            """;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String tanggalStr = rs.getString("tanggal");
                String tanggal = "--";
                String jam = "--:--:--";
                String keterangan = "";
                if (tanggalStr != null && tanggalStr.length() >= 19) {
                    tanggal = tanggalStr.substring(0, 10);
                    jam = tanggalStr.substring(11, 19);
                    if (jam.compareTo("10:00:00") > 0) {
                        keterangan = "Terlambat";
                    } else {
                        keterangan = "Tepat Waktu";
                    }
                }
                data.add(new AbsensiRecord(tanggal, jam, keterangan));
            }
            tableRiwayatAbsensi.setItems(data);
        } catch (Exception e) {
            if (labelStatus != null) labelStatus.setText("Gagal memuat riwayat absensi: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) btnAbsensi.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setMaximized(false); // reset dulu
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}