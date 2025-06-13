package util;

import model.Admin;
import model.Karyawan;
import model.UserBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class userDAO {

    // Load semua user dari database
    public static List<UserBase> loadUsers() {
        List<UserBase> users = new ArrayList<>();

        String sql = "SELECT user_id, username, password, role, gaji_per_hari FROM users";

        try (Connection conn = DataBaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");
                double gaji = rs.getDouble("gaji_per_hari");

                if (role.equalsIgnoreCase("admin")) {
                    users.add(new Admin(userId, username, password));
                } else if (role.equalsIgnoreCase("karyawan")) {
                    users.add(new Karyawan(userId, username, password, gaji));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }


    // Tambah user baru
    public static boolean addUser(String username, String password, String role, double gajiPerHari) {
        String sql = "INSERT INTO users (username, password, role, gaji_per_hari) VALUES (?, ?, ?, ?)";

        try (Connection conn = DataBaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.setDouble(4, gajiPerHari);

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Gagal menambahkan user: " + e.getMessage());
            return false;
        }
    }

    // Cari user berdasarkan username
    public static UserBase getUserByUsername(String username) {
        String sql = "SELECT user_id, username, password, role, gaji_per_hari FROM users WHERE username = ?";

        try (Connection conn = DataBaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String password = rs.getString("password");
                String role = rs.getString("role");
                double gaji = rs.getDouble("gaji_per_hari");

                if (role.equalsIgnoreCase("admin")) {
                    return new Admin(userId, username, password);
                } else if (role.equalsIgnoreCase("karyawan")) {
                    return new Karyawan(userId, username, password, gaji);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
