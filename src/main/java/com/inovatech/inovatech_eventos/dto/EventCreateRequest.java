package com.inovatech.inovatech_eventos.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para criação de eventos
 * Versão: 1.0
 * 
 * @author InovaTech Team
 */
public record EventCreateRequest(
    @NotBlank(message = "Título é obrigatório") @Size(min = 3, max = 100, message = "Título deve ter entre 3 e 100 caracteres") String title,

    @NotBlank(message = "Descrição é obrigatória") @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres") String description,

    @NotNull(message = "Data de início é obrigatória") @Future(message = "Data de início deve ser no futuro") LocalDateTime startDateTime,

    @NotNull(message = "Data de fim é obrigatória") LocalDateTime endDateTime,

    @NotBlank(message = "Local é obrigatório") @Size(min = 5, max = 200, message = "Local deve ter entre 5 e 200 caracteres") String location,

    @NotNull(message = "Capacidade é obrigatória") @Min(value = 1, message = "Capacidade deve ser no mínimo 1") Integer capacity) {
}