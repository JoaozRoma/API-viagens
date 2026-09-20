package com.agencia.viagens.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class AvaliacaoRequestDTO {

    @NotNull(message = "A nota é obrigatória.")
    @DecimalMin(value = "0.0", message = "A nota mínima permitida é 0.0.")
    @DecimalMax(value = "10.0", message = "A nota máxima permitida é 10.0.")
    private Double nota;

    public AvaliacaoRequestDTO() {
    }

    public AvaliacaoRequestDTO(Double nota) {
        this.nota = nota;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }
}
