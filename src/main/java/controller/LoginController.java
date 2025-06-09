package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.UserBase;
import model.Admin;
import model.Karyawan;
import util.FileUtil;

import java.io.IOException;
import java.util.List;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLoginAsAdmin(ActionEvent event) throws IOException {
        handleLogin("admin");
    }

    @FXML
    private void handleLoginAsKaryawan(ActionEvent event) throws IOException {
        handleLogin("karyawan");
    }

    private void handleLogin(String expectedRole) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        List<UserBase> users = FileUtil.loadUsers();
        for (UserBase user : users) {
            if (user.getUsername().equals(username) &&
                    user.getPassword().equals(password) &&
                    user.getRole().equalsIgnoreCase(expectedRole)) {

                LoginControllerHelper.setLoggedInUsername(user.getUsername());

                FXMLLoader loader = new FXMLLoader();
                if (user instanceof Admin) {
                    loader.setLocation(getClass().getResource("/view/admin.fxml"));
                } else if (user instanceof Karyawan) {
                    loader.setLocation(getClass().getResource("/view/karyawan.fxml"));
                }

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));
                return;
            }
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Login gagal: username, password, atau role tidak cocok.");
        alert.show();
    }
}
