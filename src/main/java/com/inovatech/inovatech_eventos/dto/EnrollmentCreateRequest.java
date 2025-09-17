package com.inovatech.inovatech_eventos.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para criação de inscrições
 * Versão: 1.0
 * 
 * @author InovaTech Team
 */
public record EnrollmentCreateRequest(
    @NotNull(message = "ID do evento é obrigatório") UUID eventId) {
}