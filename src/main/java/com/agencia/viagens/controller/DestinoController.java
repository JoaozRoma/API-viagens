package com.agencia.viagens.controller;

import com.agencia.viagens.dto.AvaliacaoRequestDTO;
import com.agencia.viagens.dto.DestinoRequestDTO;
import com.agencia.viagens.dto.DestinoResponseDTO;
import com.agencia.viagens.model.Destino;
import com.agencia.viagens.service.DestinoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/destinos")
public class DestinoController {

    private final DestinoService destinoService;

    public DestinoController(DestinoService destinoService) {
        this.destinoService = destinoService;
    }

    @PostMapping
    public ResponseEntity<DestinoResponseDTO> cadastrar(
            @Valid @RequestBody DestinoRequestDTO dto) {
        Destino destino = destinoService.cadastrar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(destino.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(DestinoResponseDTO.fromEntity(destino));
    }

    @GetMapping
    public ResponseEntity<List<DestinoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(mapearLista(destinoService.listarTodos()));
    }

    @GetMapping("/pesquisar")
    public ResponseEntity<List<DestinoResponseDTO>> pesquisar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String localizacao) {
        return ResponseEntity.ok(
                mapearLista(destinoService.pesquisar(nome, localizacao)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DestinoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                DestinoResponseDTO.fromEntity(destinoService.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DestinoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody DestinoRequestDTO dto) {
        return ResponseEntity.ok(
                DestinoResponseDTO.fromEntity(destinoService.atualizar(id, dto)));
    }

    @PatchMapping("/{id}/avaliar")
    public ResponseEntity<DestinoResponseDTO> avaliar(
            @PathVariable Long id,
            @Valid @RequestBody AvaliacaoRequestDTO dto) {
        return ResponseEntity.ok(
                DestinoResponseDTO.fromEntity(
                        destinoService.avaliar(id, dto.getNota())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        destinoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private List<DestinoResponseDTO> mapearLista(List<Destino> destinos) {
        return destinos.stream()
                .map(DestinoResponseDTO::fromEntity)
                .toList();
    }
}
