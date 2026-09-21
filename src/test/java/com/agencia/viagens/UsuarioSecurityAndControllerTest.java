package com.agencia.viagens;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class UsuarioSecurityAndControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Gestão de usuários sem autenticação deve retornar 401")
    void usuariosSemAutenticacaoDeveRetornar401() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("ROLE_USER não pode acessar gestão de usuários")
    void usuariosComRoleUserDeveRetornar403() throws Exception {
        mockMvc.perform(get("/api/usuarios")
                        .with(httpBasic("user", "user123")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ROLE_ADMIN pode listar usuários sem exposição de senha")
    void adminDeveListarUsuariosSemSenha() throws Exception {
        mockMvc.perform(get("/api/usuarios")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].username").exists())
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    @DisplayName("ROLE_ADMIN pode cadastrar usuário e a resposta não expõe senha")
    void adminDeveCadastrarUsuarioSemExporSenha() throws Exception {
        String json = """
                {
                  "username": "operador_teste",
                  "password": "senhaSegura123",
                  "role": "ROLE_USER"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.containsString("/api/usuarios/")))
                .andExpect(jsonPath("$.username").value("operador_teste"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("Perfil inválido deve retornar 400")
    void perfilInvalidoDeveRetornar400() throws Exception {
        String json = """
                {
                  "username": "operador_invalido",
                  "password": "senhaSegura123",
                  "role": "ADMIN"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Usuário inexistente deve retornar 404 para ADMIN")
    void usuarioInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(get("/api/usuarios/999999")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isNotFound());
    }
}
