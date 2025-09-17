package com.inovatech.inovatech_eventos.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inovatech.inovatech_eventos.model.TypeUser;
import com.inovatech.inovatech_eventos.model.User;

/**
 * DTO para resposta de usuários
 * Versão: 1.0
 * 
 * @author InovaTech Team
 */
public record UserResponse(
    UUID id,
    String name,
    String email,
    TypeUser type,
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime createdAt,
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime updatedAt
) {
    /**
     * Converter User para UserResponse
     */
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getType(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}