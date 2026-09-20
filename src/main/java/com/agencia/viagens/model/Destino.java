package com.agencia.viagens.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_destinos")
public class Destino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do destino é obrigatório.")
    @Size(max = 150, message = "O nome não pode ter mais de 150 caracteres.")
    @Column(nullable = false, length = 150)
    private String nome;

    @NotBlank(message = "A localização do destino é obrigatória.")
    @Size(max = 150, message = "A localização não pode ter mais de 150 caracteres.")
    @Column(nullable = false, length = 150)
    private String localizacao;

    @Size(max = 1000, message = "A descrição não pode ter mais de 1000 caracteres.")
    @Column(length = 1000)
    private String descricao;

    @Column(nullable = false)
    private double mediaAvaliacoes;

    @Column(nullable = false)
    private int quantidadeAvaliacoes;

    public Destino() {
        this.mediaAvaliacoes = 0.0;
        this.quantidadeAvaliacoes = 0;
    }

    public Destino(String nome, String localizacao, String descricao) {
        this();
        this.nome = nome;
        this.localizacao = localizacao;
        this.descricao = descricao;
    }

    public void registrarAvaliacao(double nota) {
        double somaAtual = this.mediaAvaliacoes * this.quantidadeAvaliacoes;
        somaAtual += nota;
        this.quantidadeAvaliacoes++;
        this.mediaAvaliacoes = Math.round((somaAtual / this.quantidadeAvaliacoes) * 10.0) / 10.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
