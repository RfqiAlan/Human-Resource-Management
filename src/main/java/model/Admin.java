package model;

public class Admin extends UserBase {
    public Admin(String username, String password, String role) {
        super(username, password, role);
    }

    @Override
    public double getGajiPerHari() {
        return 0; // Admin tidak dihitung gaji harian dalam sistem ini
    }
}