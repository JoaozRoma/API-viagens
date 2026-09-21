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
        validarDTO(dto);
        Destino destino = new Destino(
                normalizarObrigatorio(dto.getNome(), "nome", 150),
                normalizarObrigatorio(dto.getLocalizacao(), "localização", 150),
                normalizarOpcional(dto.getDescricao(), "descrição", 1000));

        return destinoRepository.save(destino);
    }

    @Transactional(readOnly = true)
    public List<Destino> listarTodos() {
        return destinoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Destino buscarPorId(Long id) {
        return destinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Destino não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Destino> pesquisar(String nome, String localizacao) {
        String nomeFiltro = normalizarFiltro(nome);
        String localizacaoFiltro = normalizarFiltro(localizacao);

        if (nomeFiltro == null && localizacaoFiltro == null) {
            return destinoRepository.findAll();
        }
        if (nomeFiltro != null && localizacaoFiltro != null) {
            return destinoRepository
                    .findByNomeContainingIgnoreCaseAndLocalizacaoContainingIgnoreCase(
                            nomeFiltro,
                            localizacaoFiltro);
        }
        if (nomeFiltro != null) {
            return destinoRepository.findByNomeContainingIgnoreCase(nomeFiltro);
        }
        return destinoRepository
                .findByLocalizacaoContainingIgnoreCase(localizacaoFiltro);
    }

    @Transactional
    public Destino atualizar(Long id, DestinoRequestDTO dto) {
        validarDTO(dto);
        Destino destino = buscarPorId(id);
        destino.setNome(normalizarObrigatorio(dto.getNome(), "nome", 150));
        destino.setLocalizacao(
                normalizarObrigatorio(dto.getLocalizacao(), "localização", 150));
        destino.setDescricao(
                normalizarOpcional(dto.getDescricao(), "descrição", 1000));

        return destinoRepository.save(destino);
    }

    @Transactional
    public Destino avaliar(Long id, Double nota) {
        if (nota == null || !Double.isFinite(nota)
                || nota < 0.0 || nota > 10.0) {
            throw new BusinessException(
                    "A nota de avaliação deve estar entre 0.0 e 10.0.");
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

    private void validarDTO(DestinoRequestDTO dto) {
        if (dto == null) {
            throw new BusinessException("Os dados do destino são obrigatórios.");
        }
    }

    private String normalizarObrigatorio(String valor,
                                         String campo,
                                         int tamanhoMaximo) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException(
                    "O campo " + campo + " é obrigatório.");
        }

        String normalizado = valor.trim();
        if (normalizado.length() > tamanhoMaximo) {
            throw new BusinessException(
                    "O campo " + campo + " excede "
                            + tamanhoMaximo + " caracteres.");
        }
        return normalizado;
    }

    private String normalizarOpcional(String valor,
                                      String campo,
                                      int tamanhoMaximo) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        String normalizado = valor.trim();
        if (normalizado.length() > tamanhoMaximo) {
            throw new BusinessException(
                    "O campo " + campo + " excede "
                            + tamanhoMaximo + " caracteres.");
        }
        return normalizado;
    }

    private String normalizarFiltro(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
