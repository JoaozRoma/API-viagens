package com.agencia.viagens;

import com.agencia.viagens.dto.UsuarioRequestDTO;
import com.agencia.viagens.dto.UsuarioResponseDTO;
import com.agencia.viagens.exception.BusinessException;
import com.agencia.viagens.model.Role;
import com.agencia.viagens.model.Usuario;
import com.agencia.viagens.repository.UsuarioRepository;
import com.agencia.viagens.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Deve cadastrar usuário com senha BCrypt e username normalizado")
    void deveCadastrarUsuarioComSenhaCriptografada() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "  novousuario  ",
                "senhaSegura123",
                Role.ROLE_USER);

        UsuarioResponseDTO salvo = usuarioService.cadastrar(dto);

        assertNotNull(salvo.getId());
        assertEquals("novousuario", salvo.getUsername());
        assertEquals(Role.ROLE_USER, salvo.getRole());

        Usuario usuarioBanco = usuarioRepository
                .findByUsername("novousuario")
                .orElseThrow();
        assertNotEquals("senhaSegura123", usuarioBanco.getPassword());
        assertTrue(passwordEncoder.matches(
                "senhaSegura123", usuarioBanco.getPassword()));
    }

    @Test
    @DisplayName("Deve impedir username duplicado após normalização")
    void deveImpedirUsernameDuplicado() {
        usuarioService.cadastrar(new UsuarioRequestDTO(
                "usuarioDuplicado", "senha12345", Role.ROLE_USER));

        assertThrows(BusinessException.class,
                () -> usuarioService.cadastrar(new UsuarioRequestDTO(
                        "  usuarioDuplicado  ",
                        "outraSenha123",
                        Role.ROLE_USER)));
    }


    @Test
    @DisplayName("Deve rejeitar username que fica curto após remover espaços")
    void deveRejeitarUsernameCurtoAposNormalizacao() {
        assertThrows(BusinessException.class,
                () -> usuarioService.cadastrar(new UsuarioRequestDTO(
                        "  a  ", "senha123", Role.ROLE_USER)));
    }

    @Test
    @DisplayName("Deve rejeitar perfil nulo na camada de serviço")
    void deveRejeitarPerfilNulo() {
        assertThrows(BusinessException.class,
                () -> usuarioService.cadastrar(new UsuarioRequestDTO(
                        "semperfil", "senha123", null)));
    }

    @Test
    @DisplayName("Deve carregar usuário do banco para autenticação")
    void deveCarregarUsuarioPorUsername() {
        usuarioService.cadastrar(new UsuarioRequestDTO(
                "authuser", "senha123", Role.ROLE_ADMIN));

        UserDetails userDetails = usuarioService
                .loadUserByUsername("authuser");

        assertEquals("authuser", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Deve lançar exceção ao autenticar usuário inexistente")
    void deveLancarExcecaoAoBuscarUsuarioInexistente() {
        assertThrows(UsernameNotFoundException.class,
                () -> usuarioService.loadUserByUsername("inexistente"));
    }
}
