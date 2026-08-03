package com.agencia.viagens.service;

import com.agencia.viagens.model.Destino;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DestinoService {
    private final Map<String, Destino> destinos = new HashMap<>();

    public Destino cadastrar(Destino destino) {
        destinos.put(destino.getId(), destino);
        return destino;
    }

    public List<Destino> listarTodos() {
        return new ArrayList<>(destinos.values());
    }

    public List<Destino> pesquisar(String nome, String localizacao) {
        return destinos.values().stream()
                .filter(d -> (nome == null || d.getNome().toLowerCase().contains(nome.toLowerCase())) &&
                        (localizacao == null || d.getLocalizacao().toLowerCase().contains(localizacao.toLowerCase())))
                .toList();
    }

    public Optional<Destino> buscarPorId(String id) {
        return Optional.ofNullable(destinos.get(id));
    }

    public Destino atualizar(String id, Destino destinoAtualizado) {
        if (destinos.containsKey(id)) {
            Destino existente = destinos.get(id);
            destinoAtualizado.setId(id);
            destinoAtualizado.setMediaAvaliacoes(existente.getMediaAvaliacoes());
            destinoAtualizado.setQuantidadeAvaliacoes(existente.getQuantidadeAvaliacoes());

            destinos.put(id, destinoAtualizado);
            return destinoAtualizado;
        }
        return null;
    }

    public Destino avaliar(String id, double nota) {
        Destino destino = destinos.get(id);
        if (destino != null) {
            destino.registrarAvaliacao(nota);
            return destino;
        }
        return null;
    }

    public boolean excluir(String id) {
        return destinos.remove(id) != null;
    }
}