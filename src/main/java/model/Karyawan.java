package model;

public class Karyawan extends UserBase {
    private double gajiPerHari;

    public Karyawan(int userId, String username, String password, double gajiPerHari) {
        super(userId, username, password, "karyawan");
        this.gajiPerHari = gajiPerHari;
    }
    @Override
    public double getGajiPerHari() {
        return gajiPerHari;
    }
    @Override
    public String getInfo() {
        return String.format("Gaji: Rp%.0f", gajiPerHari);
    }
}