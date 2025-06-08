package model;

public class Karyawan extends UserBase {
    private double gajiPerHari;

    public Karyawan(String username, String password, String role, double gajiPerHari) {
        super(username, password, role);
        this.gajiPerHari = gajiPerHari;
    }

    @Override
    public double getGajiPerHari() {
        return gajiPerHari;
    }
}