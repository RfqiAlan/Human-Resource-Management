package model;

public abstract class UserBase {
    protected String username;
    protected String password;
    protected String role;

    public UserBase(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public abstract double getGajiPerHari();
}