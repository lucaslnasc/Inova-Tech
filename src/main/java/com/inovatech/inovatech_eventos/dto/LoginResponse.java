package com.inovatech.inovatech_eventos.dto;

/**
 * DTO para resposta de login
 * Versão: 1.0
 * 
 * @author InovaTech Team
 */
public record LoginResponse(
    UserResponse user,
    String token,
    String message) {
}