package com.inovatech.inovatech_eventos.dto;

import com.inovatech.inovatech_eventos.model.TypeUser;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para criação de usuários
 * Versão: 1.0
 * 
 * @author InovaTech Team
 */
public record UserCreateRequest(
    @NotBlank(message = "O nome é obrigatório") @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres") String name,

    @NotBlank(message = "O email é obrigatório") @Email(message = "Email deve ter um formato válido") @Size(max = 100, message = "Email deve ter no máximo 100 caracteres") String email,

    @NotBlank(message = "A senha é obrigatória") @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres") String password,

    @NotNull(message = "O tipo de usuário é obrigatório") TypeUser type) {
}