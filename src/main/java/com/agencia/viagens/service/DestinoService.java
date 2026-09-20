package com.agencia.viagens.service;

import com.agencia.viagens.dto.DestinoRequestDTO;
import com.agencia.viagens.exception.BusinessException;
import com.agencia.viagens.exception.ResourceNotFoundException;
import com.agencia.viagens.model.Destino;
import com.agencia.viagens.repository.DestinoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DestinoService {

    private final DestinoRepository destinoRepository;

    public DestinoService(DestinoRepository destinoRepository) {
        this.destinoRepository = destinoRepository;
    }

    @Transactional
    public Destino cadastrar(DestinoRequestDTO dto) {
        Destino destino = new Destino(dto.getNome(), dto.getLocalizacao(), dto.getDescricao());
        return destinoRepository.save(destino);
    }

    @Transactional(readOnly = true)
    public List<Destino> listarTodos() {
        return destinoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Destino buscarPorId(Long id) {
        return destinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destino não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Destino> pesquisar(String nome, String localizacao) {
        String nomeFiltro = (nome != null && !nome.isBlank()) ? nome.trim() : null;
        String localizacaoFiltro = (localizacao != null && !localizacao.isBlank()) ? localizacao.trim() : null;

        if (nomeFiltro == null && localizacaoFiltro == null) {
            return destinoRepository.findAll();
        }

        return destinoRepository.pesquisarPorNomeELocalizacao(nomeFiltro, localizacaoFiltro);
    }

    @Transactional
    public Destino atualizar(Long id, DestinoRequestDTO dto) {
        Destino destino = buscarPorId(id);
        destino.setNome(dto.getNome());
        destino.setLocalizacao(dto.getLocalizacao());
        destino.setDescricao(dto.getDescricao());
        return destinoRepository.save(destino);
    }

    @Transactional
    public Destino avaliar(Long id, Double nota) {
        if (nota == null || nota < 0.0 || nota > 10.0) {
            throw new BusinessException("A nota de avaliação deve estar entre 0.0 e 10.0.");
        }

        Destino destino = buscarPorId(id);
        destino.registrarAvaliacao(nota);
        return destinoRepository.save(destino);
    }

    @Transactional
    public void excluir(Long id) {
        Destino destino = buscarPorId(id);
        destinoRepository.delete(destino);
    }
}
