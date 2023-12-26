package com.example.examenfinal;

public class Ubicacion {
    private double latitude;
    private double longitude;

    public Ubicacion() {
        // Constructor sin argumentos requerido por Firebase
    }

    public Ubicacion(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
