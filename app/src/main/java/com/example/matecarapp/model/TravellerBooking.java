package com.example.matecarapp.model;

public class TravellerBooking {

    String idTraveller;
    String idDriver;
    String destination;
    String source;
    String km;
    String status;
    double sourceLat;
    double sourceLng;
    double destinationLat;
    double destinationLng;

    public TravellerBooking(String id, String idTraveller, String idDriver, String destination, String source, String km, String status, double sourceLat, double sourceLng, double destinationLat, double destinationLng) {

        this.idTraveller = idTraveller;
        this.idDriver = idDriver;
        this.destination = destination;
        this.source = source;
        this.km = km;
        this.status = status;
        this.sourceLat = sourceLat;
        this.sourceLng = sourceLng;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
    }

    public String getIdTraveller() {
        return idTraveller;
    }

    public void setIdTraveller(String idTraveller) {
        this.idTraveller = idTraveller;
    }

    public String getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(String idDriver) {
        this.idDriver = idDriver;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getSourceLat() {
        return sourceLat;
    }

    public void setSourceLat(double sourceLat) {
        this.sourceLat = sourceLat;
    }

    public double getSourceLng() {
        return sourceLng;
    }

    public void setSourceLng(double sourceLng) {
        this.sourceLng = sourceLng;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public double getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(double destinationLng) {
        this.destinationLng = destinationLng;
    }
}
