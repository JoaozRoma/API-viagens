package com.agencia.viagens.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    protected Destino() {
        // Construtor exigido pela JPA.
    }

    public Destino(String nome, String localizacao, String descricao) {
        this.nome = nome;
        this.localizacao = localizacao;
        this.descricao = descricao;
        this.mediaAvaliacoes = 0.0;
        this.quantidadeAvaliacoes = 0;
    }

    public void registrarAvaliacao(double nota) {
        double somaAtual = mediaAvaliacoes * quantidadeAvaliacoes;
        int novaQuantidade = quantidadeAvaliacoes + 1;

        mediaAvaliacoes = (somaAtual + nota) / novaQuantidade;
        quantidadeAvaliacoes = novaQuantidade;
    }

    public Long getId() {
        return id;
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

    public int getQuantidadeAvaliacoes() {
        return quantidadeAvaliacoes;
    }
}
