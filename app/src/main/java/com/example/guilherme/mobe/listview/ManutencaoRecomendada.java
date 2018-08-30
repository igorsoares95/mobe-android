package com.example.guilherme.mobe.listview;

import java.io.Serializable;

public class ManutencaoRecomendada implements Serializable {

    String id_manutencao_padrao, descricao, limite_km, limite_tempo_meses;
    boolean selecionado;

    public ManutencaoRecomendada(String id_manutencao_padrao, String descricao, String limite_km, String limite_tempo_meses, boolean selecionado) {
        this.id_manutencao_padrao = id_manutencao_padrao;
        this.descricao = descricao;
        this.limite_km = limite_km;
        this.limite_tempo_meses = limite_tempo_meses;
        this.selecionado = selecionado;
    }

    public String getId_manutencao_padrao() {
        return id_manutencao_padrao;
    }

    public void setId_manutencao_padrao(String id_manutencao_padrao) {
        this.id_manutencao_padrao = id_manutencao_padrao;
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

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }
}
