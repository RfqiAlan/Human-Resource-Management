package model;

public abstract class UserBase {
    protected int userId;
    protected String username;
    protected String password;
    protected String role;

    public UserBase(int userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
    public abstract double getGajiPerHari();

    // Opsional: untuk menampilkan info
    public String getInfo() {
        return String.format("%s| (%s)", username, role);
    }
}
