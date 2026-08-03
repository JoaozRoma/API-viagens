package com.agencia.viagens.model;

import java.util.UUID;

public class Destino {
    private String id;
    private String nome;
    private String localizacao;
    private String descricao;
    private double mediaAvaliacoes;
    private int quantidadeAvaliacoes;

    public Destino() {
        this.id = UUID.randomUUID().toString();
        this.mediaAvaliacoes = 0.0;
        this.quantidadeAvaliacoes = 0;
    }


    public void registrarAvaliacao(double nota) {
        double somaAtual = this.mediaAvaliacoes * this.quantidadeAvaliacoes;
        somaAtual += nota;
        this.quantidadeAvaliacoes++;
        this.mediaAvaliacoes = somaAtual / this.quantidadeAvaliacoes;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getMediaAvaliacoes() {
        return mediaAvaliacoes;
    }

    public void setMediaAvaliacoes(double mediaAvaliacoes) {
        this.mediaAvaliacoes = mediaAvaliacoes;
    }

    public int getQuantidadeAvaliacoes() {
        return quantidadeAvaliacoes;
    }

    public void setQuantidadeAvaliacoes(int quantidadeAvaliacoes) {
        this.quantidadeAvaliacoes = quantidadeAvaliacoes;
    }
}