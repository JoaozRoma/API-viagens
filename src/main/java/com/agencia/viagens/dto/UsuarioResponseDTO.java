package com.agencia.viagens.dto;

import com.agencia.viagens.model.Role;
import com.agencia.viagens.model.Usuario;

public class UsuarioResponseDTO {

    private Long id;
    private String username;
    private Role role;

    public UsuarioResponseDTO() {
    }

    public UsuarioResponseDTO(Long id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getId(), usuario.getUsername(), usuario.getRole());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
