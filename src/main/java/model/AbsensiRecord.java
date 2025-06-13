package model;

import javafx.beans.property.SimpleStringProperty;

public class AbsensiRecord {
    private final SimpleStringProperty username;
    private final SimpleStringProperty tanggal;
    private final SimpleStringProperty jam;
    private final SimpleStringProperty keterangan;

    // Constructor untuk AdminController
    public AbsensiRecord(String username, String tanggal, String jam, String keterangan) {
        this.username = new SimpleStringProperty(username);
        this.tanggal = new SimpleStringProperty(tanggal);
        this.jam = new SimpleStringProperty(jam);
        this.keterangan = new SimpleStringProperty(keterangan);
    }

    // Constructor untuk KaryawanController (tanpa username)
    public AbsensiRecord(String tanggal, String jam, String keterangan) {
        this.username = new SimpleStringProperty("");
        this.tanggal = new SimpleStringProperty(tanggal);
        this.jam = new SimpleStringProperty(jam);
        this.keterangan = new SimpleStringProperty(keterangan);
    }

    public String getUsername() { return username.get(); }
    public String getTanggal() { return tanggal.get(); }
    public String getJam() { return jam.get(); }
    public String getKeterangan() { return keterangan.get(); }

    public SimpleStringProperty tanggalProperty() { return tanggal; }
    public SimpleStringProperty jamProperty() { return jam; }
    public SimpleStringProperty keteranganProperty() { return keterangan; }
}