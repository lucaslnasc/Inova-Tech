package com.inovatech.inovatech_eventos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resposta padrão da API
 * Versão: 1.0
 * 
 * @param <T> Tipo dos dados da resposta
 * @author InovaTech Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  private boolean success;
  private String message;
  private T data;
  private String error;

  /**
   * Criar resposta de sucesso
   */
  public static <T> ApiResponse<T> success(String message, T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .message(message)
        .data(data)
        .build();
  }

  /**
   * Criar resposta de erro
   */
  public static <T> ApiResponse<T> error(String error) {
    return ApiResponse.<T>builder()
        .success(false)
        .error(error)
        .build();
  }
}