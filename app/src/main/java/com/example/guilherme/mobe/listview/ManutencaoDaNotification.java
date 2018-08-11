package com.example.guilherme.mobe.listview;

public class ManutencaoDaNotification {

    private String modelo;
    private String placa;
    private String km_atual;
    private String descricao_manutencao;
    private String km_ultima_manutencao;
    private String km_proxima_manutencao;
    private String data_ultima_manutencao;
    private String data_proxima_manutencao;
    private String status;

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

    public String getKm_atual() {
        return km_atual;
    }

    public void setKm_atual(String km_atual) {
        this.km_atual = km_atual;
    }

    public String getDescricao_manutencao() {
        return descricao_manutencao;
    }

    public void setDescricao_manutencao(String descricao_manutencao) {
        this.descricao_manutencao = descricao_manutencao;
    }

    public String getKm_ultima_manutencao() {
        return km_ultima_manutencao;
    }

    public void setKm_ultima_manutencao(String km_ultima_manutencao) {
        this.km_ultima_manutencao = km_ultima_manutencao;
    }

    public String getKm_proxima_manutencao() {
        return km_proxima_manutencao;
    }

    public void setKm_proxima_manutencao(String km_proxima_manutencao) {
        this.km_proxima_manutencao = km_proxima_manutencao;
    }

    public String getData_ultima_manutencao() {
        return data_ultima_manutencao;
    }

    public void setData_ultima_manutencao(String data_ultima_manutencao) {
        this.data_ultima_manutencao = data_ultima_manutencao;
    }

    public String getData_proxima_manutencao() {
        return data_proxima_manutencao;
    }

    public void setData_proxima_manutencao(String data_proxima_manutencao) {
        this.data_proxima_manutencao = data_proxima_manutencao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ManutencaoDaNotification(String modelo, String placa, String km_atual, String descricao_manutencao, String km_ultima_manutencao, String km_proxima_manutencao, String data_ultima_manutencao, String data_proxima_manutencao, String status) {
        this.modelo = modelo;
        this.placa = placa;
        this.km_atual = km_atual;
        this.descricao_manutencao = descricao_manutencao;
        this.km_ultima_manutencao = km_ultima_manutencao;
        this.km_proxima_manutencao = km_proxima_manutencao;
        this.data_ultima_manutencao = data_ultima_manutencao;
        this.data_proxima_manutencao = data_proxima_manutencao;
        this.status = status;
    }

}
