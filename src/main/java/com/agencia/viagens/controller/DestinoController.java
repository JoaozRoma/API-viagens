package com.agencia.viagens.controller;

import com.agencia.viagens.model.Destino;
import com.agencia.viagens.service.DestinoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/destinos")
public class DestinoController {

    private final DestinoService service;

    public DestinoController(DestinoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Destino> cadastrar(@RequestBody Destino destino) {
        Destino novoDestino = service.cadastrar(destino);
        return new ResponseEntity<>(novoDestino, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Destino>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/pesquisar")
    public ResponseEntity<List<Destino>> pesquisar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String localizacao) {
        return ResponseEntity.ok(service.pesquisar(nome, localizacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Destino> buscarPorId(@PathVariable String id) {
        Optional<Destino> destino = service.buscarPorId(id);
        return destino.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Destino> atualizar(@PathVariable String id, @RequestBody Destino destino) {
        Destino atualizado = service.atualizar(id, destino);
        if (atualizado != null) {
            return ResponseEntity.ok(atualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/avaliar")
    public ResponseEntity<Destino> avaliar(@PathVariable String id, @RequestBody Map<String, Double> payload) {
        Double nota = payload.get("nota");
        if (nota == null || nota < 0 || nota > 10) {
            return ResponseEntity.badRequest().build();
        }

        Destino destino = service.avaliar(id, nota);
        if (destino != null) {
            return ResponseEntity.ok(destino);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        if (service.excluir(id)) {
            return ResponseEntity.noContent().build(); // 204 Sucesso e sem corpo de resposta
        }
        return ResponseEntity.notFound().build();
    }
}