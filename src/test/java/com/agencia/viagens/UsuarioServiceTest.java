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
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve cadastrar um novo usuário com senha criptografada")
    void deveCadastrarUsuarioComSenhaCriptografada() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO("novousuario", "senhaSegura123", Role.ROLE_USER);
        UsuarioResponseDTO salvo = usuarioService.cadastrar(dto);

        assertNotNull(salvo.getId());
        assertEquals("novousuario", salvo.getUsername());
        assertEquals(Role.ROLE_USER, salvo.getRole());

        Usuario usuarioBanco = usuarioRepository.findByUsername("novousuario").orElseThrow();
        assertNotEquals("senhaSegura123", usuarioBanco.getPassword());
        assertTrue(usuarioBanco.getPassword().startsWith("$2a$") || usuarioBanco.getPassword().startsWith("$2b$") || usuarioBanco.getPassword().startsWith("$2y$"));
    }

    @Test
    @DisplayName("Deve impedir cadastro de usuário com username duplicado")
    void deveImpedirUsernameDuplicado() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO("usuarioDuplicado", "senha12345", Role.ROLE_USER);
        usuarioService.cadastrar(dto);

        assertThrows(BusinessException.class, () -> usuarioService.cadastrar(dto));
    }

    @Test
    @DisplayName("Deve carregar usuário por username através do UserDetailsService")
    void deveCarregarUsuarioPorUsername() {
        usuarioService.cadastrar(new UsuarioRequestDTO("authuser", "senha123", Role.ROLE_ADMIN));

        UserDetails userDetails = usuarioService.loadUserByUsername("authuser");
        assertNotNull(userDetails);
        assertEquals("authuser", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente")
    void deveLancarExcecaoAoBuscarUsuarioInexistente() {
        assertThrows(UsernameNotFoundException.class, () -> usuarioService.loadUserByUsername("inexistente"));
    }
}
