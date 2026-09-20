package com.agencia.viagens.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DestinoRequestDTO {

    @NotBlank(message = "O nome do destino é obrigatório.")
    @Size(max = 150, message = "O nome não pode exceder 150 caracteres.")
    private String nome;

    @NotBlank(message = "A localização do destino é obrigatória.")
    @Size(max = 150, message = "A localização não pode exceder 150 caracteres.")
    private String localizacao;

    @Size(max = 1000, message = "A descrição não pode exceder 1000 caracteres.")
    private String descricao;

    public DestinoRequestDTO() {
    }

    public DestinoRequestDTO(String nome, String localizacao, String descricao) {
        this.nome = nome;
        this.localizacao = localizacao;
        this.descricao = descricao;
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
}
