package com.inovatech.inovatech_eventos.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * DTO para atualização de eventos
 * Versão: 1.0
 * 
 * @author InovaTech Team
 */
public record EventUpdateRequest(
    @Size(min = 3, max = 100, message = "Título deve ter entre 3 e 100 caracteres") String title,

    @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres") String description,

    LocalDateTime startDateTime,

    LocalDateTime endDateTime,

    @Size(min = 5, max = 200, message = "Local deve ter entre 5 e 200 caracteres") String location,

    @Min(value = 1, message = "Capacidade deve ser no mínimo 1") Integer capacity,

    Boolean isActive) {
}