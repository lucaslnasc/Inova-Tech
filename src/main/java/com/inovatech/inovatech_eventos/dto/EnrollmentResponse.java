package com.inovatech.inovatech_eventos.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.inovatech.inovatech_eventos.model.EnrollmentStatus;

/**
 * DTO para resposta de inscrições
 * Versão: 1.0
 * 
 * @author InovaTech Team
 */
public record EnrollmentResponse(
    UUID id,
    UUID eventId,
    String eventTitle,
    LocalDateTime eventStartDateTime,
    String eventLocation,
    UUID participantId,
    String participantName,
    String participantEmail,
    EnrollmentStatus status,
    LocalDateTime enrollmentDate) {
}