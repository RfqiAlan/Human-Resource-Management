package model;

public class CutiItem {
    private int userId;
    private String username;
    private String alasan;
    private String status;

    public CutiItem(int userId, String username, String alasan, String status) {
        this.userId = userId;
        this.username = username;
        this.alasan = alasan;
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public String getAlasan() {
        return alasan;
    }

    @Override
    public String toString() {
        return "Karyawan: " + username + " | Alasan: " + alasan + " | Status : " + status;
    }
}
