package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AdminController {
    @FXML private TextField newUsername;
    @FXML private PasswordField newPassword;
    @FXML private TextField newGaji;
    @FXML private Button btnDaftar;
    @FXML private TextArea areaStatus;
    @FXML private TextArea areaAbsensiHariIni;

    @FXML
    private void handleDaftar() {
        String user = newUsername.getText();
        String pass = newPassword.getText();
        String gajiStr = newGaji.getText();

        try {
            double gaji = Double.parseDouble(gajiStr);
            String newData = user + "," + pass + ",karyawan," + gaji + "\n";
            java.nio.file.Files.write(java.nio.file.Paths.get("data/users.txt"), newData.getBytes(), java.nio.file.StandardOpenOption.APPEND);
            areaStatus.setText("Berhasil mendaftarkan: " + user);
        } catch (Exception e) {
            areaStatus.setText("Gagal mendaftar: " + e.getMessage());
        }
    }

    @FXML
    private void handleLihatAbsensiHariIni() {
        try {
            String today = LocalDate.now().toString();
            List<String> allLines = Files.readAllLines(Paths.get("data/absensi.txt"));
            List<String> todayAbsensi = allLines.stream()
                    .filter(line -> line.contains("," + today + ","))
                    .collect(Collectors.toList());
            areaAbsensiHariIni.setText(String.join("\n", todayAbsensi));
        } catch (Exception e) {
            areaAbsensiHariIni.setText("Gagal load absensi hari ini: " + e.getMessage());
        }
    }
}
