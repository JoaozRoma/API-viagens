package com.agencia.viagens;

import com.agencia.viagens.dto.DestinoRequestDTO;
import com.agencia.viagens.exception.BusinessException;
import com.agencia.viagens.exception.ResourceNotFoundException;
import com.agencia.viagens.model.Destino;
import com.agencia.viagens.repository.DestinoRepository;
import com.agencia.viagens.service.DestinoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DestinoServiceTest {

    @Autowired
    private DestinoService destinoService;

    @Autowired
    private DestinoRepository destinoRepository;

    @BeforeEach
    void setUp() {
        destinoRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve cadastrar um novo destino com sucesso")
    void deveCadastrarDestinoComSucesso() {
        DestinoRequestDTO dto = new DestinoRequestDTO("Florianópolis", "Santa Catarina, Brasil", "Ilha da Magia.");
        Destino salvo = destinoService.cadastrar(dto);

        assertNotNull(salvo.getId());
        assertEquals("Florianópolis", salvo.getNome());
        assertEquals("Santa Catarina, Brasil", salvo.getLocalizacao());
        assertEquals(0.0, salvo.getMediaAvaliacoes());
        assertEquals(0, salvo.getQuantidadeAvaliacoes());
    }

    @Test
    @DisplayName("Deve listar todos os destinos")
    void deveListarTodosOsDestinos() {
        destinoService.cadastrar(new DestinoRequestDTO("Destino A", "Local A", "Desc A"));
        destinoService.cadastrar(new DestinoRequestDTO("Destino B", "Local B", "Desc B"));

        List<Destino> lista = destinoService.listarTodos();
        assertEquals(2, lista.size());
    }

    @Test
    @DisplayName("Deve buscar destino por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        Destino salvo = destinoService.cadastrar(new DestinoRequestDTO("Curitiba", "Paraná", "Cidade Jardim"));
        Destino encontrado = destinoService.buscarPorId(salvo.getId());

        assertNotNull(encontrado);
        assertEquals(salvo.getId(), encontrado.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar destino por ID inexistente")
    void deveLancarExcecaoAoBuscarIdInexistente() {
        assertThrows(ResourceNotFoundException.class, () -> destinoService.buscarPorId(99999L));
    }

    @Test
    @DisplayName("Deve pesquisar destinos por nome e localização")
    void devePesquisarDestinos() {
        destinoService.cadastrar(new DestinoRequestDTO("Praia de Pipa", "Rio Grande do Norte", "Praia incrível"));
        destinoService.cadastrar(new DestinoRequestDTO("Praia do Rosa", "Santa Catarina", "Praia de surf"));
        destinoService.cadastrar(new DestinoRequestDTO("Campos do Jordão", "São Paulo", "Cidade serrana"));

        List<Destino> resultadoNome = destinoService.pesquisar("pipa", null);
        assertEquals(1, resultadoNome.size());
        assertEquals("Praia de Pipa", resultadoNome.get(0).getNome());

        List<Destino> resultadoLoc = destinoService.pesquisar(null, "catarina");
        assertEquals(1, resultadoLoc.size());
        assertEquals("Praia do Rosa", resultadoLoc.get(0).getNome());
    }

    @Test
    @DisplayName("Deve atualizar destino existente")
    void deveAtualizarDestinoComSucesso() {
        Destino salvo = destinoService.cadastrar(new DestinoRequestDTO("Nome Antigo", "Local Antigo", "Desc Antiga"));
        DestinoRequestDTO atualizacao = new DestinoRequestDTO("Nome Novo", "Local Novo", "Desc Nova");

        Destino atualizado = destinoService.atualizar(salvo.getId(), atualizacao);

        assertEquals("Nome Novo", atualizado.getNome());
        assertEquals("Local Novo", atualizado.getLocalizacao());
        assertEquals("Desc Nova", atualizado.getDescricao());
    }

    @Test
    @DisplayName("Deve avaliar destino e recalcular a média corretamente")
    void deveAvaliarDestinoERecalcularMedia() {
        Destino salvo = destinoService.cadastrar(new DestinoRequestDTO("Bonito", "Mato Grosso do Sul", "Ecoturismo"));

        Destino d1 = destinoService.avaliar(salvo.getId(), 8.0);
        assertEquals(8.0, d1.getMediaAvaliacoes());
        assertEquals(1, d1.getQuantidadeAvaliacoes());

        Destino d2 = destinoService.avaliar(salvo.getId(), 10.0);
        assertEquals(9.0, d2.getMediaAvaliacoes());
        assertEquals(2, d2.getQuantidadeAvaliacoes());
    }

    @Test
    @DisplayName("Deve lançar exceção para notas de avaliação inválidas")
    void deveLancarExcecaoParaNotasInvalidas() {
        Destino salvo = destinoService.cadastrar(new DestinoRequestDTO("Manaus", "Amazonas", "Floresta Amazônica"));

        assertThrows(BusinessException.class, () -> destinoService.avaliar(salvo.getId(), -1.0));
        assertThrows(BusinessException.class, () -> destinoService.avaliar(salvo.getId(), 10.1));
    }

    @Test
    @DisplayName("Deve excluir destino com sucesso")
    void deveExcluirDestinoComSucesso() {
        Destino salvo = destinoService.cadastrar(new DestinoRequestDTO("Foz do Iguaçu", "Paraná", "Cataratas"));
        destinoService.excluir(salvo.getId());

        assertThrows(ResourceNotFoundException.class, () -> destinoService.buscarPorId(salvo.getId()));
    }
}
