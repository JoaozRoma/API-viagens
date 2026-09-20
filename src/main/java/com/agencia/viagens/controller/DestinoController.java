package com.agencia.viagens.controller;

import com.agencia.viagens.dto.AvaliacaoRequestDTO;
import com.agencia.viagens.dto.DestinoRequestDTO;
import com.agencia.viagens.exception.BusinessException;
import com.agencia.viagens.model.Destino;
import com.agencia.viagens.service.DestinoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinos")
public class DestinoController {

    private final DestinoService destinoService;

    public DestinoController(DestinoService destinoService) {
        this.destinoService = destinoService;
    }

    @PostMapping
    public ResponseEntity<Destino> cadastrar(@Valid @RequestBody DestinoRequestDTO dto) {
        Destino destino = destinoService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(destino);
    }

    @GetMapping
    public ResponseEntity<List<Destino>> listarTodos() {
        return ResponseEntity.ok(destinoService.listarTodos());
    }

    @GetMapping("/pesquisar")
    public ResponseEntity<List<Destino>> pesquisar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String localizacao) {
        return ResponseEntity.ok(destinoService.pesquisar(nome, localizacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Destino> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(destinoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Destino> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody DestinoRequestDTO dto) {
        return ResponseEntity.ok(destinoService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/avaliar")
    public ResponseEntity<Destino> avaliar(
            @PathVariable Long id,
            @RequestBody(required = false) AvaliacaoRequestDTO body,
            @RequestParam(required = false) Double nota) {
        Double valorNota = null;
        if (body != null && body.getNota() != null) {
            valorNota = body.getNota();
        } else if (nota != null) {
            valorNota = nota;
        } else {
            throw new BusinessException("A nota de avaliação é obrigatória (via JSON no corpo da requisição ou parâmetro de URL).");
        }

        return ResponseEntity.ok(destinoService.avaliar(id, valorNota));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        destinoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
