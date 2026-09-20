package com.agencia.viagens;

import com.agencia.viagens.dto.DestinoRequestDTO;
import com.agencia.viagens.model.Destino;
import com.agencia.viagens.repository.DestinoRepository;
import com.agencia.viagens.service.DestinoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class DestinoSecurityAndControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private DestinoService destinoService;

    @Autowired
    private DestinoRepository destinoRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("GET /api/destinos deve ser público (sem autenticação)")
    void getDestinosDeveSerPublico() throws Exception {
        mockMvc.perform(get("/api/destinos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/destinos sem autenticação deve retornar 401 Unauthorized")
    void postDestinoSemAutenticacaoDeveRetornar401() throws Exception {
        String json = """
                {
                    "nome": "Novo Destino",
                    "localizacao": "Local",
                    "descricao": "Desc"
                }
                """;

        mockMvc.perform(post("/api/destinos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/destinos com ROLE_USER deve retornar 403 Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void postDestinoComRoleUserDeveRetornar403() throws Exception {
        String json = """
                {
                    "nome": "Novo Destino",
                    "localizacao": "Local",
                    "descricao": "Desc"
                }
                """;

        mockMvc.perform(post("/api/destinos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/destinos com ROLE_ADMIN deve retornar 201 Created")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void postDestinoComRoleAdminDeveRetornar201() throws Exception {
        String json = """
                {
                    "nome": "Destino Admin",
                    "localizacao": "Local Admin",
                    "descricao": "Desc Admin"
                }
                """;

        mockMvc.perform(post("/api/destinos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Destino Admin"));
    }

    @Test
    @DisplayName("PATCH /api/destinos/{id}/avaliar com ROLE_USER deve retornar 200 OK")
    @WithMockUser(username = "user", roles = {"USER"})
    void patchAvaliarComRoleUserDeveRetornar200() throws Exception {
        Destino destino = destinoService.cadastrar(new DestinoRequestDTO("Destino Teste", "Local", "Desc"));

        mockMvc.perform(patch("/api/destinos/" + destino.getId() + "/avaliar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nota\": 9.5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mediaAvaliacoes").value(9.5))
                .andExpect(jsonPath("$.quantidadeAvaliacoes").value(1));
    }

    @Test
    @DisplayName("DELETE /api/destinos/{id} com ROLE_USER deve retornar 403 Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void deleteComRoleUserDeveRetornar403() throws Exception {
        Destino destino = destinoService.cadastrar(new DestinoRequestDTO("Destino Excluir", "Local", "Desc"));

        mockMvc.perform(delete("/api/destinos/" + destino.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/destinos/{id} com ROLE_ADMIN deve retornar 204 No Content")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteComRoleAdminDeveRetornar204() throws Exception {
        Destino destino = destinoService.cadastrar(new DestinoRequestDTO("Destino Deletar", "Local", "Desc"));

        mockMvc.perform(delete("/api/destinos/" + destino.getId()))
                .andExpect(status().isNoContent());
    }
}
