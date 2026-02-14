package per.example.dersnotlar_apporn;

import java.io.Serializable;

public class Notlar implements Serializable {
    private String not_id;
    private String ders_adi;
    private int not1;
    private int not2;

    public Notlar() {
    }

    public Notlar(String notID, String ders_adi, int not1, int not2) {
        this.not_id = notID;
        this.ders_adi = ders_adi;
        this.not1 = not1;
        this.not2 = not2;
    }

    public String getNotID() {
        return not_id;
    }

    public void setNotID(String notID) {
        this.not_id = notID;
    }

    public String getDersAdi() {
        return ders_adi;
    }

    public void setDersAdi(String dersAdi) {
        this.ders_adi = dersAdi;
    }

    public int getNot1() {
        return not1;
    }

    public void setNot1(int not1) {
        this.not1 = not1;
    }

    public int getNot2() {
        return not2;
    }

    public void setNot2(int not2) {
        this.not2 = not2;
    }
}
