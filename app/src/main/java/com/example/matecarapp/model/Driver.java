package com.example.matecarapp.model;

public class Driver {
    String id;
    String ad;
    String email;
    String arabaModeli;
    String arabaPlakasi;

    public Driver(String id, String ad, String email, String arabaModeli, String arabaPlakasi) {

        this.id = id;
        this.ad = ad;
        this.email = email;
        this.arabaModeli = arabaModeli;
        this.arabaPlakasi = arabaPlakasi;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getArabaModeli() {
        return arabaModeli;
    }

    public void setArabaModeli(String arabaModeli) {
        this.arabaModeli = arabaModeli;
    }

    public String getArabaPlakasi() {
        return arabaPlakasi;
    }

    public void setArabaPlakasi(String arabaPlakasi) {
        this.arabaPlakasi = arabaPlakasi;
    }
}
