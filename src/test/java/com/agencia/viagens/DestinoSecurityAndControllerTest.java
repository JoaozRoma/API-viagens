package com.agencia.viagens;

import com.agencia.viagens.dto.DestinoRequestDTO;
import com.agencia.viagens.model.Destino;
import com.agencia.viagens.service.DestinoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class DestinoSecurityAndControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private DestinoService destinoService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("GET /api/destinos deve ser público")
    void getDestinosDeveSerPublico() throws Exception {
        mockMvc.perform(get("/api/destinos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST sem autenticação deve retornar 401")
    void postDestinoSemAutenticacaoDeveRetornar401() throws Exception {
        mockMvc.perform(post("/api/destinos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(destinoJson("Novo Destino")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Credenciais inválidas devem retornar 401")
    void credenciaisInvalidasDevemRetornar401() throws Exception {
        mockMvc.perform(post("/api/destinos")
                        .with(httpBasic("admin", "senha-incorreta"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(destinoJson("Novo Destino")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("ROLE_USER não pode cadastrar destino")
    void postDestinoComRoleUserDeveRetornar403() throws Exception {
        mockMvc.perform(post("/api/destinos")
                        .with(httpBasic("user", "user123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(destinoJson("Destino Bloqueado")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ROLE_ADMIN deve cadastrar destino e receber Location")
    void postDestinoComRoleAdminDeveRetornar201() throws Exception {
        mockMvc.perform(post("/api/destinos")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(destinoJson("Destino Admin")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.containsString("/api/destinos/")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Destino Admin"));
    }

    @Test
    @DisplayName("ROLE_USER não pode atualizar destino")
    void putDestinoComRoleUserDeveRetornar403() throws Exception {
        Destino destino = destinoService.cadastrar(
                new DestinoRequestDTO("Destino Atualizar", "Local", "Descrição"));

        mockMvc.perform(put("/api/destinos/" + destino.getId())
                        .with(httpBasic("user", "user123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(destinoJson("Destino Alterado")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ROLE_ADMIN deve atualizar destino")
    void putDestinoComRoleAdminDeveRetornar200() throws Exception {
        Destino destino = destinoService.cadastrar(
                new DestinoRequestDTO("Destino Atualizar", "Local", "Descrição"));

        mockMvc.perform(put("/api/destinos/" + destino.getId())
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(destinoJson("Destino Alterado")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Destino Alterado"));
    }

    @Test
    @DisplayName("ROLE_USER deve conseguir avaliar destino")
    void patchAvaliarComRoleUserDeveRetornar200() throws Exception {
        Destino destino = destinoService.cadastrar(
                new DestinoRequestDTO("Destino Teste", "Local", "Descrição"));

        mockMvc.perform(patch("/api/destinos/" + destino.getId() + "/avaliar")
                        .with(httpBasic("user", "user123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nota\": 9.5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mediaAvaliacoes").value(9.5))
                .andExpect(jsonPath("$.quantidadeAvaliacoes").value(1));
    }

    @Test
    @DisplayName("Avaliação sem autenticação deve retornar 401")
    void patchAvaliarSemAutenticacaoDeveRetornar401() throws Exception {
        Destino destino = destinoService.cadastrar(
                new DestinoRequestDTO("Destino Teste", "Local", "Descrição"));

        mockMvc.perform(patch("/api/destinos/" + destino.getId() + "/avaliar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nota\": 9.5}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Avaliação fora do intervalo deve retornar 400")
    void patchAvaliacaoInvalidaDeveRetornar400() throws Exception {
        Destino destino = destinoService.cadastrar(
                new DestinoRequestDTO("Destino Teste", "Local", "Descrição"));

        mockMvc.perform(patch("/api/destinos/" + destino.getId() + "/avaliar")
                        .with(httpBasic("user", "user123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nota\": 10.1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.nota").exists());
    }

    @Test
    @DisplayName("ROLE_USER não pode excluir destino")
    void deleteComRoleUserDeveRetornar403() throws Exception {
        Destino destino = destinoService.cadastrar(
                new DestinoRequestDTO("Destino Excluir", "Local", "Descrição"));

        mockMvc.perform(delete("/api/destinos/" + destino.getId())
                        .with(httpBasic("user", "user123")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ROLE_ADMIN deve excluir destino")
    void deleteComRoleAdminDeveRetornar204() throws Exception {
        Destino destino = destinoService.cadastrar(
                new DestinoRequestDTO("Destino Deletar", "Local", "Descrição"));

        mockMvc.perform(delete("/api/destinos/" + destino.getId())
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Destino inexistente deve retornar 404")
    void getDestinoInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(get("/api/destinos/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private String destinoJson(String nome) {
        return """
                {
                  "nome": "%s",
                  "localizacao": "Local de Teste",
                  "descricao": "Descrição de teste"
                }
                """.formatted(nome);
    }
}
