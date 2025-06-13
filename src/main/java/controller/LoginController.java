package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.UserBase;
import model.Admin;
import model.Karyawan;
import util.userDAO;

import java.io.IOException;
import java.util.List;

public class LoginController {

    public HBox cardGroup;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLoginAsAdmin(ActionEvent ignoredEvent) throws IOException {
        handleLogin("admin");
    }

    @FXML
    private void handleLoginAsKaryawan(ActionEvent ignoredEvent) throws IOException {
        handleLogin("karyawan");
    }

    private void handleLogin(String expectedRole) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        List<UserBase> users = userDAO.loadUsers();
        for (UserBase user : users) {
            if (user.getUsername().equals(username) &&
                    user.getPassword().equals(password) &&
                    user.getRole().equalsIgnoreCase(expectedRole)) {

                LoginControllerHelper.setLoggedInUsername(username);

                FXMLLoader loader = new FXMLLoader();
                if (user instanceof Admin) {
                    loader.setLocation(getClass().getResource("/view/admin.fxml"));
                } else if (user instanceof Karyawan) {
                    loader.setLocation(getClass().getResource("/view/karyawan.fxml"));
                }

                // Tampilkan halaman sesuai role
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));
                stage.setMaximized(false); // reset dulu
                javafx.application.Platform.runLater(() -> stage.setMaximized(true));
                return; // <<=== Penting agar tidak lanjut ke alert error
            }
        }

        // Hanya muncul jika tidak ada user yang cocok
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Login gagal: username, password, atau role tidak cocok.");
        alert.show();
    }
}
