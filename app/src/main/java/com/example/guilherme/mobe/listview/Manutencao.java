package com.example.guilherme.mobe.listview;

public class Manutencao {

    private String id, descricao, limite_km, limite_tempo_meses, km_antecipacao, tempo_antecipacao_meses, data_ultima_manutencao, km_ultima_manutencao;

    public Manutencao(String id, String descricao, String limite_km, String limite_tempo_meses, String km_antecipacao, String tempo_antecipacao_meses, String data_ultima_manutencao, String km_ultima_manutencao) {
        this.id = id;
        this.descricao = descricao;
        this.limite_km = limite_km;
        this.limite_tempo_meses = limite_tempo_meses;
        this.km_antecipacao = km_antecipacao;
        this.tempo_antecipacao_meses = tempo_antecipacao_meses;
        this.data_ultima_manutencao = data_ultima_manutencao;
        this.km_ultima_manutencao = km_ultima_manutencao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLimite_km() {
        return limite_km;
    }

    public void setLimite_km(String limite_km) {
        this.limite_km = limite_km;
    }

    public String getLimite_tempo_meses() {
        return limite_tempo_meses;
    }

    public void setLimite_tempo_meses(String limite_tempo_meses) {
        this.limite_tempo_meses = limite_tempo_meses;
    }

    public String getKm_antecipacao() {
        return km_antecipacao;
    }

    public void setKm_antecipacao(String km_antecipacao) {
        this.km_antecipacao = km_antecipacao;
    }

    public String getTempo_antecipacao_meses() {
        return tempo_antecipacao_meses;
    }

    public void setTempo_antecipacao_meses(String tempo_antecipacao_meses) {
        this.tempo_antecipacao_meses = tempo_antecipacao_meses;
    }

    public String getData_ultima_manutencao() {
        return data_ultima_manutencao;
    }

    public void setData_ultima_manutencao(String data_ultima_manutencao) {
        this.data_ultima_manutencao = data_ultima_manutencao;
    }

    public String getKm_ultima_manutencao() {
        return km_ultima_manutencao;
    }

    public void setKm_ultima_manutencao(String km_ultima_manutencao) {
        this.km_ultima_manutencao = km_ultima_manutencao;
    }
}
