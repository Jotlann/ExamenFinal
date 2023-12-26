package com.example.examenfinal;

import java.util.ArrayList;
import java.util.List;

public class HistorialRutas {
    private String nombre;
    private String fecha;
    private String coordenadasIniciales;
    private String coordenadasFinales;
    private long duracion;
    private double distanciaTotal;

    private List<HistorialRutas> historialRutasList;


    public HistorialRutas() {

    }

    public HistorialRutas(String nombre, String fecha, String posicionInicial, String posicionFinal, long duracion, double distanciaTotal) {
        this.nombre = (nombre != null) ? nombre : "";
        this.fecha = (fecha != null) ? fecha : "";
        this.coordenadasIniciales = (posicionInicial != null) ? posicionInicial : "";
        this.coordenadasFinales = (posicionFinal != null) ? posicionFinal : "";
        historialRutasList = new ArrayList<>();
        this.duracion = duracion;
        this.distanciaTotal = distanciaTotal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = (nombre != null) ? nombre : "";
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = (fecha != null) ? fecha : "";
    }

    public String getCoordenadasIniciales() {
        return coordenadasIniciales;
    }

    public void setCoordenadasIniciales(String coordenadasIniciales) {
        this.coordenadasIniciales = (coordenadasIniciales != null) ? coordenadasIniciales : "";
    }

    public String getCoordenadasFinales() {
        return coordenadasFinales;
    }

    public void setCoordenadasFinales(String coordenadasFinales) {
        this.coordenadasFinales = (coordenadasFinales != null) ? coordenadasFinales : "";
    }

    public long getTiempoContadorSegundos() {
        return duracion;
    }

    public void setTiempoContadorSegundos(long tiempoContadorSegundos) {
        this.duracion = tiempoContadorSegundos;
    }

    public double getDistanciaTotal() {
        return distanciaTotal;
    }

    public void setDistanciaTotal(float distanciaTotal) {
        this.distanciaTotal = distanciaTotal;
    }
}


