package com.example.guilherme.mobe.listview;

/**
 * Created by igorsoares on 13/07/2018.
 */

public class Veiculo {

    private String modelo;
    private String placa;
    private String km;
    private String dispositivo;

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    public Veiculo(String modelo, String placa, String km, String dispositivo) {

        this.modelo = modelo;
        this.placa = placa;
        this.km = km;
        this.dispositivo = dispositivo;

    }

}
