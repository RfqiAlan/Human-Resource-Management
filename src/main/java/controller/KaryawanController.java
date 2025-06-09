package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.UserBase;
import model.UserBase;
import util.FileUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KaryawanController {
    @FXML private Label labelStatus;
    @FXML private Button btnAbsensi;
    @FXML private TextField fieldCuti;
    @FXML private Button btnAjukanCuti;
    @FXML private TextArea areaCutiStatus;
    @FXML private Button btnLihatGaji;
    @FXML private Label labelGaji;
    @FXML private Button btnLogout;

    private String username = "";

    @FXML
    public void initialize() {
        username = LoginControllerHelper.getLoggedInUsername();
    }

    @FXML
    private void handleAbsensi() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        String record = "Karyawan: " + username  + ", Absensi: " + formattedDateTime + "\n";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/absensi.txt", true))) {
            bw.write(record);
            labelStatus.setText("Absensi berhasil untuk " + username);
        } catch (Exception e) {
            labelStatus.setText("Gagal absensi: " + e.getMessage());
        }
    }
    @FXML
    private void handleAjukanCuti() {
        String alasan = fieldCuti.getText();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/cuti.txt", true))) {
            bw.write(username + "," + alasan + ",proses\n");
            areaCutiStatus.setText("Cuti diajukan dengan alasan: " + alasan);
        } catch (Exception e) {
            areaCutiStatus.setText("Gagal ajukan cuti: " + e.getMessage());
        }
    }

    @FXML
    private void handleLihatCuti() {
        try {
            List<String> list = Files.readAllLines(Paths.get("data/cuti.txt"));
            String status = list.stream()
                    .filter(line -> line.startsWith(username + ","))
                    .map(line -> line.split(","))
                    .map(arr -> "Alasan: " + arr[1] + " | Status: " + arr[2])
                    .collect(Collectors.joining("\n"));
            areaCutiStatus.setText(status);
        } catch (Exception e) {
            areaCutiStatus.setText("Gagal lihat cuti: " + e.getMessage());
        }
    }

    @FXML
    private void handleLihatGaji() {
        try {
            List<String> absensiLines = Files.readAllLines(Paths.get("data/absensi.txt"));
            Set<String> tanggalUnik = absensiLines.stream()
                    .filter(line -> line.startsWith("Karyawan: " + username))
                    .map(line -> line.split("Absensi: ")[1].substring(0, 10)) // Ambil tanggal saja
                    .collect(Collectors.toSet());

            double gajiPerHari = FileUtil.loadUsers().stream()
                    .filter(u -> u.getUsername().equals(username))
                    .mapToDouble(UserBase::getGajiPerHari)
                    .findFirst()
                    .orElse(0);

            double total = tanggalUnik.size() * gajiPerHari;
            labelGaji.setText("Gaji saat ini: Rp " + total + ", Dengan total hari kerja: " + tanggalUnik.size());
        } catch (Exception e) {
            labelGaji.setText("Gagal hitung gaji: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) btnAbsensi.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}