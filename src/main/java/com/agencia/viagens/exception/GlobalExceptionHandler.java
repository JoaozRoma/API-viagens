package com.agencia.viagens.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex) {
        return criarResposta(
                HttpStatus.NOT_FOUND,
                "Não Encontrado",
                ex.getMessage(),
                null);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(
            BusinessException ex) {
        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Requisição Inválida",
                ex.getMessage(),
                null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(error.getField(), error.getDefaultMessage());
        }

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Erro de Validação",
                "Um ou mais campos contêm erros de validação.",
                campos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableBody(
            HttpMessageNotReadableException ex) {
        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Requisição Inválida",
                "O corpo JSON está ausente, malformado ou contém valor incompatível.",
                null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Requisição Inválida",
                "O parâmetro '" + ex.getName() + "' possui formato inválido.",
                null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            ConstraintViolationException ex) {
        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Erro de Validação",
                "Um ou mais valores violam as regras de validação.",
                null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(
            DataIntegrityViolationException ex) {
        return criarResposta(
                HttpStatus.CONFLICT,
                "Conflito de Dados",
                "A operação viola uma restrição de integridade do banco de dados.",
                null);
    }

    private ResponseEntity<Map<String, Object>> criarResposta(
            HttpStatus status,
            String erro,
            String mensagem,
            Map<String, String> campos) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", erro);
        body.put("message", mensagem);
        if (campos != null && !campos.isEmpty()) {
            body.put("fields", campos);
        }

        return ResponseEntity.status(status).body(body);
    }
}
