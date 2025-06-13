package model;

public class Admin extends UserBase {

    public Admin(int userId, String username, String password) {
        super(userId, username, password, "admin");
    }

    @Override
    public double getGajiPerHari() {
        return 0;
    }
    @Override
    public String getInfo() {
        return "Admin: " + username;
    }

}
