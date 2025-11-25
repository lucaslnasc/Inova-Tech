package com.inovatech.inovatech_eventos.dto;

import com.inovatech.inovatech_eventos.model.TypeUser;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * DTO para atualização de usuários
 * Versão: 1.0
 * 
 * @author InovaTech Team
 */
public record UserUpdateRequest(
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres") String name,

    @Email(message = "Email deve ter um formato válido") @Size(max = 100, message = "Email deve ter no máximo 100 caracteres") String email,

    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres") String password,

    TypeUser type) {
}