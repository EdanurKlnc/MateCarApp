package com.example.matecarapp.model;

public class Traveller {

    String id;
    String ad;
    String email;

    public Traveller(String id, String ad, String email) {
        this.id = id;
        this.ad = ad;
        this.email = email;
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
}
