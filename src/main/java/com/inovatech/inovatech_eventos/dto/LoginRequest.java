package com.inovatech.inovatech_eventos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para requisição de login
 * Versão: 1.0
 * 
 * @author InovaTech Team
 */
public record LoginRequest(
    @NotBlank(message = "O email é obrigatório") @Email(message = "Email deve ter um formato válido") String email,

    @NotBlank(message = "A senha é obrigatória") String password) {
}