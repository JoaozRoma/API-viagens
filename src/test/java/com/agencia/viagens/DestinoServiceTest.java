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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @DisplayName("Deve cadastrar um novo destino com os dados normalizados")
    void deveCadastrarDestinoComSucesso() {
        DestinoRequestDTO dto = new DestinoRequestDTO(
                "  Florianópolis  ",
                "  Santa Catarina, Brasil  ",
                "  Ilha da Magia.  ");

        Destino salvo = destinoService.cadastrar(dto);

        assertNotNull(salvo.getId());
        assertEquals("Florianópolis", salvo.getNome());
        assertEquals("Santa Catarina, Brasil", salvo.getLocalizacao());
        assertEquals("Ilha da Magia.", salvo.getDescricao());
        assertEquals(0.0, salvo.getMediaAvaliacoes());
        assertEquals(0, salvo.getQuantidadeAvaliacoes());
    }


    @Test
    @DisplayName("Deve rejeitar DTO nulo e nome acima do limite")
    void deveRejeitarDadosDeDestinoInvalidosNaCamadaDeServico() {
        assertThrows(BusinessException.class,
                () -> destinoService.cadastrar(null));
        assertThrows(BusinessException.class,
                () -> destinoService.cadastrar(new DestinoRequestDTO(
                        "X".repeat(151), "Local", "Descrição")));
    }

    @Test
    @DisplayName("Deve listar todos os destinos")
    void deveListarTodosOsDestinos() {
        destinoService.cadastrar(
                new DestinoRequestDTO("Destino A", "Local A", "Desc A"));
        destinoService.cadastrar(
                new DestinoRequestDTO("Destino B", "Local B", "Desc B"));

        assertEquals(2, destinoService.listarTodos().size());
    }

    @Test
    @DisplayName("Deve buscar destino por ID")
    void deveBuscarPorIdComSucesso() {
        Destino salvo = destinoService.cadastrar(
                new DestinoRequestDTO("Curitiba", "Paraná", "Cidade Jardim"));

        Destino encontrado = destinoService.buscarPorId(salvo.getId());

        assertEquals(salvo.getId(), encontrado.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção para ID inexistente")
    void deveLancarExcecaoAoBuscarIdInexistente() {
        assertThrows(ResourceNotFoundException.class,
                () -> destinoService.buscarPorId(99999L));
    }

    @Test
    @DisplayName("Deve pesquisar por nome, localização e combinação dos filtros")
    void devePesquisarDestinos() {
        destinoService.cadastrar(new DestinoRequestDTO(
                "Praia de Pipa", "Rio Grande do Norte", "Praia incrível"));
        destinoService.cadastrar(new DestinoRequestDTO(
                "Praia do Rosa", "Santa Catarina", "Praia de surf"));
        destinoService.cadastrar(new DestinoRequestDTO(
                "Campos do Jordão", "São Paulo", "Cidade serrana"));

        List<Destino> porNome = destinoService.pesquisar("pipa", null);
        List<Destino> porLocal = destinoService.pesquisar(null, "catarina");
        List<Destino> combinada = destinoService.pesquisar("praia", "catarina");
        List<Destino> semFiltros = destinoService.pesquisar("  ", null);

        assertEquals(1, porNome.size());
        assertEquals("Praia de Pipa", porNome.get(0).getNome());
        assertEquals(1, porLocal.size());
        assertEquals("Praia do Rosa", porLocal.get(0).getNome());
        assertEquals(1, combinada.size());
        assertEquals("Praia do Rosa", combinada.get(0).getNome());
        assertEquals(3, semFiltros.size());
    }

    @Test
    @DisplayName("Deve atualizar descrição sem alterar histórico de avaliações")
    void deveAtualizarDestinoSemPerderAvaliacao() {
        Destino salvo = destinoService.cadastrar(new DestinoRequestDTO(
                "Nome Antigo", "Local Antigo", "Desc Antiga"));
        destinoService.avaliar(salvo.getId(), 8.0);

        Destino atualizado = destinoService.atualizar(
                salvo.getId(),
                new DestinoRequestDTO("Nome Novo", "Local Novo", "Desc Nova"));

        assertEquals("Nome Novo", atualizado.getNome());
        assertEquals("Local Novo", atualizado.getLocalizacao());
        assertEquals("Desc Nova", atualizado.getDescricao());
        assertEquals(8.0, atualizado.getMediaAvaliacoes());
        assertEquals(1, atualizado.getQuantidadeAvaliacoes());
    }

    @Test
    @DisplayName("Deve recalcular a média sem erro cumulativo de arredondamento")
    void deveCalcularMediaSemDerivaDeArredondamento() {
        Destino salvo = destinoService.cadastrar(new DestinoRequestDTO(
                "Bonito", "Mato Grosso do Sul", "Ecoturismo"));

        destinoService.avaliar(salvo.getId(), 0.0);
        destinoService.avaliar(salvo.getId(), 0.0);
        destinoService.avaliar(salvo.getId(), 1.0);
        Destino avaliado = destinoService.avaliar(salvo.getId(), 2.0);

        assertEquals(0.75, avaliado.getMediaAvaliacoes(), 0.000000001);
        assertEquals(4, avaliado.getQuantidadeAvaliacoes());
    }

    @Test
    @DisplayName("Deve rejeitar notas nulas, não finitas e fora do intervalo")
    void deveLancarExcecaoParaNotasInvalidas() {
        Destino salvo = destinoService.cadastrar(new DestinoRequestDTO(
                "Manaus", "Amazonas", "Floresta Amazônica"));

        assertThrows(BusinessException.class,
                () -> destinoService.avaliar(salvo.getId(), null));
        assertThrows(BusinessException.class,
                () -> destinoService.avaliar(salvo.getId(), -1.0));
        assertThrows(BusinessException.class,
                () -> destinoService.avaliar(salvo.getId(), 10.1));
        assertThrows(BusinessException.class,
                () -> destinoService.avaliar(salvo.getId(), Double.NaN));
        assertThrows(BusinessException.class,
                () -> destinoService.avaliar(
                        salvo.getId(), Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("Deve excluir destino")
    void deveExcluirDestinoComSucesso() {
        Destino salvo = destinoService.cadastrar(new DestinoRequestDTO(
                "Foz do Iguaçu", "Paraná", "Cataratas"));

        destinoService.excluir(salvo.getId());

        assertThrows(ResourceNotFoundException.class,
                () -> destinoService.buscarPorId(salvo.getId()));
    }
}
