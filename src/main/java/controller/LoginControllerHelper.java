package controller;

import model.UserBase;

public class LoginControllerHelper {
    private static String loggedInUsername;

    public static void setLoggedInUsername(String username) {
        loggedInUsername = username;
    }
    public static String getLoggedInUsername() {
        return loggedInUsername;
    }
}