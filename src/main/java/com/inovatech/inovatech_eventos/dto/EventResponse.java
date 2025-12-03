package com.inovatech.inovatech_eventos.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para resposta de eventos
 * Versão: 1.0
 * 
 * @author InovaTech Team
 */
public record EventResponse(
    UUID id,
    String title,
    String description,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime,
    String location,
    Integer capacity,
    Integer currentEnrollments,
    Boolean isActive,
    UUID organizerId,
    String organizerName,
    String organizerEmail) {
}