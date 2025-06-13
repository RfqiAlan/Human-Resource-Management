package util;

import java.sql.*;

public class DataBaseHelper {
    private static final String DB_URL = "jdbc:sqlite:Database/Data.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeTables() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            // Tabel users: user_id sebagai primary key
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "role TEXT NOT NULL CHECK(role IN ('admin', 'karyawan'))," +
                    "gaji_per_hari REAL)");

            // Tabel absensi dengan user_id sebagai foreign key
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS absensi (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "tanggal TEXT," +
                    "FOREIGN KEY(user_id) REFERENCES users(user_id))");

            // Tabel cuti dengan user_id sebagai foreign key
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS cuti (" +
                    "cuti_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "alasan TEXT," +
                    "status TEXT," +
                    "FOREIGN KEY(user_id) REFERENCES users(user_id))");

            insertDefaultAdmin();

            System.out.println("Tabel berhasil disiapkan.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertDefaultAdmin() {
        String sql = "INSERT OR IGNORE INTO users (username, password, role, gaji_per_hari) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "admin");
            pstmt.setString(2, "admin123"); // Ganti ke hash password untuk keamanan produksi
            pstmt.setString(3, "admin");
            pstmt.setDouble(4, 0); // Gaji 0 karena admin tidak pakai
            pstmt.executeUpdate();

            System.out.println("Admin default ditambahkan (jika belum ada).");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
