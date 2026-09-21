package com.agencia.viagens.dto;

import com.agencia.viagens.model.Destino;

public class DestinoResponseDTO {

    private final Long id;
    private final String nome;
    private final String localizacao;
    private final String descricao;
    private final double mediaAvaliacoes;
    private final int quantidadeAvaliacoes;

    public DestinoResponseDTO(Long id,
                              String nome,
                              String localizacao,
                              String descricao,
                              double mediaAvaliacoes,
                              int quantidadeAvaliacoes) {
        this.id = id;
        this.nome = nome;
        this.localizacao = localizacao;
        this.descricao = descricao;
        this.mediaAvaliacoes = mediaAvaliacoes;
        this.quantidadeAvaliacoes = quantidadeAvaliacoes;
    }

    public static DestinoResponseDTO fromEntity(Destino destino) {
        return new DestinoResponseDTO(
                destino.getId(),
                destino.getNome(),
                destino.getLocalizacao(),
                destino.getDescricao(),
                destino.getMediaAvaliacoes(),
                destino.getQuantidadeAvaliacoes());
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getMediaAvaliacoes() {
        return mediaAvaliacoes;
    }

    public int getQuantidadeAvaliacoes() {
        return quantidadeAvaliacoes;
    }
}
