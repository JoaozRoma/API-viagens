package com.agencia.viagens.service;

import com.agencia.viagens.dto.UsuarioRequestDTO;
import com.agencia.viagens.dto.UsuarioResponseDTO;
import com.agencia.viagens.exception.BusinessException;
import com.agencia.viagens.exception.ResourceNotFoundException;
import com.agencia.viagens.model.Usuario;
import com.agencia.viagens.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + username));
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
        if (dto == null) {
            throw new BusinessException("Os dados do usuário são obrigatórios.");
        }

        String username = normalizarUsername(dto.getUsername());
        validarSenha(dto.getPassword());
        if (dto.getRole() == null) {
            throw new BusinessException("O perfil de acesso é obrigatório.");
        }

        if (usuarioRepository.existsByUsername(username)) {
            throw new BusinessException(
                    "Nome de usuário já cadastrado no sistema.");
        }

        Usuario usuario = new Usuario(
                username,
                passwordEncoder.encode(dto.getPassword()),
                dto.getRole());

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::fromEntity)
                .toList();
    }

    private String normalizarUsername(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException("O nome de usuário é obrigatório.");
        }

        String username = valor.trim();
        if (username.length() < 3 || username.length() > 50) {
            throw new BusinessException(
                    "O nome de usuário deve ter entre 3 e 50 caracteres.");
        }
        return username;
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.isBlank() || senha.length() < 6) {
            throw new BusinessException(
                    "A senha deve ter no mínimo 6 caracteres.");
        }
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário não encontrado com ID: " + id));

        return UsuarioResponseDTO.fromEntity(usuario);
    }
}
