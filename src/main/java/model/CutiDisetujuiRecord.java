package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class CutiDisetujuiRecord {
    private final SimpleIntegerProperty cutiId;
    private final SimpleStringProperty username;
    private final SimpleStringProperty alasan;

    public CutiDisetujuiRecord(int cutiId, String username, String alasan) {
        this.cutiId = new SimpleIntegerProperty(cutiId);
        this.username = new SimpleStringProperty(username);
        this.alasan = new SimpleStringProperty(alasan);
    }
    public int getCutiId() {
        return cutiId.get();
    }
    public String getUsername() {
        return username.get();
    }
    public SimpleStringProperty usernameProperty() {
        return username;
    }
    public SimpleStringProperty alasanProperty() {
        return alasan;
    }
}