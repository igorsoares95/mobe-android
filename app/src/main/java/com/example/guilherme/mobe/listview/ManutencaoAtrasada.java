package com.example.guilherme.mobe.listview;

public class ManutencaoAtrasada {

    private String id, descricao, veiculo, placa, km_atual, km_proxima_manutencao, data_proxima_manutencao;

    public ManutencaoAtrasada(String id, String descricao, String veiculo, String placa, String km_atual, String km_proxima_manutencao, String data_proxima_manutencao) {

        this.id = id;
        this.descricao = descricao;
        this.veiculo = veiculo;
        this.placa = placa;
        this.km_atual = km_atual;
        this.km_proxima_manutencao = km_proxima_manutencao;
        this.data_proxima_manutencao = data_proxima_manutencao;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKm_atual() {
        return km_atual;
    }

    public void setKm_atual(String km_atual) {
        this.km_atual = km_atual;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(String veiculo) {
        this.veiculo = veiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getKm_proxima_manutencao() {
        return km_proxima_manutencao;
    }

    public void setKm_proxima_manutencao(String km_proxima_manutencao) {
        this.km_proxima_manutencao = km_proxima_manutencao;
    }

    public String getData_proxima_manutencao() {
        return data_proxima_manutencao;
    }

    public void setData_proxima_manutencao(String data_proxima_manutencao) {
        this.data_proxima_manutencao = data_proxima_manutencao;
    }
}
