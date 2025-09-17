package com.inovatech.inovatech_eventos.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inovatech.inovatech_eventos.dto.ApiResponse;
import com.inovatech.inovatech_eventos.dto.EnrollmentCreateRequest;
import com.inovatech.inovatech_eventos.dto.EnrollmentResponse;
import com.inovatech.inovatech_eventos.service.EnrollmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST para gerenciamento de inscrições
 * Versão: 1.0
 * 
 * @author InovaTech Team
 */
@RestController
@RequestMapping("/api/v1/enrollments")
@Tag(name = "Enrollments", description = "API de gerenciamento de inscrições em eventos")
@SecurityRequirement(name = "bearerAuth")
public class EnrollmentController {

  @Autowired
  private EnrollmentService enrollmentService;

  /**
   * Criar nova inscrição
   */
  @PostMapping
  @Operation(summary = "Inscrever-se em evento", description = "Cria uma nova inscrição do usuário em um evento")
  public ResponseEntity<ApiResponse<EnrollmentResponse>> createEnrollment(
      @Valid @RequestBody EnrollmentCreateRequest request,
      Authentication authentication) {
    try {
      String userEmail = authentication.getName();
      EnrollmentResponse enrollment = enrollmentService.createEnrollment(request, userEmail);

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(ApiResponse.success("Inscrição realizada com sucesso", enrollment));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("Erro ao realizar inscrição: " + e.getMessage()));
    }
  }

  /**
   * Listar inscrições do usuário
   */
  @GetMapping("/my-enrollments")
  @Operation(summary = "Minhas inscrições", description = "Lista todas as inscrições do usuário autenticado")
  public ResponseEntity<ApiResponse<Page<EnrollmentResponse>>> getMyEnrollments(
      Authentication authentication,
      @Parameter(description = "Parâmetros de paginação") Pageable pageable) {
    try {
      String userEmail = authentication.getName();
      Page<EnrollmentResponse> enrollments = enrollmentService.getEnrollmentsByUser(userEmail, pageable);
      return ResponseEntity.ok(ApiResponse.success("Inscrições listadas com sucesso", enrollments));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("Erro ao listar inscrições: " + e.getMessage()));
    }
  }

  /**
   * Listar inscrições de um evento
   */
  @GetMapping("/event/{eventId}")
  @Operation(summary = "Inscrições do evento", description = "Lista inscrições de um evento específico (apenas organizador)")
  public ResponseEntity<ApiResponse<Page<EnrollmentResponse>>> getEnrollmentsByEvent(
      @Parameter(description = "ID do evento") @PathVariable UUID eventId,
      Authentication authentication,
      @Parameter(description = "Parâmetros de paginação") Pageable pageable) {
    try {
      String userEmail = authentication.getName();
      Page<EnrollmentResponse> enrollments = enrollmentService.getEnrollmentsByEvent(eventId, userEmail, pageable);
      return ResponseEntity.ok(ApiResponse.success("Inscrições do evento listadas", enrollments));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("Erro ao listar inscrições: " + e.getMessage()));
    }
  }

  /**
   * Cancelar inscrição
   */
  @DeleteMapping("/{enrollmentId}")
  @Operation(summary = "Cancelar inscrição", description = "Cancela uma inscrição existente")
  public ResponseEntity<ApiResponse<Void>> cancelEnrollment(
      @Parameter(description = "ID da inscrição") @PathVariable UUID enrollmentId,
      Authentication authentication) {
    try {
      String userEmail = authentication.getName();
      enrollmentService.cancelEnrollment(enrollmentId, userEmail);

      return ResponseEntity.ok(ApiResponse.<Void>success("Inscrição cancelada com sucesso", null));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("Erro ao cancelar inscrição: " + e.getMessage()));
    }
  }

  /**
   * Confirmar inscrição (organizador)
   */
  @PutMapping("/{enrollmentId}/confirm")
  @Operation(summary = "Confirmar inscrição", description = "Confirma uma inscrição pendente (apenas organizador)")
  public ResponseEntity<ApiResponse<EnrollmentResponse>> confirmEnrollment(
      @Parameter(description = "ID da inscrição") @PathVariable UUID enrollmentId,
      Authentication authentication) {
    try {
      String userEmail = authentication.getName();
      EnrollmentResponse enrollment = enrollmentService.confirmEnrollment(enrollmentId, userEmail);

      return ResponseEntity.ok(ApiResponse.success("Inscrição confirmada com sucesso", enrollment));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("Erro ao confirmar inscrição: " + e.getMessage()));
    }
  }

  /**
   * Rejeitar inscrição (organizador)
   */
  @PutMapping("/{enrollmentId}/reject")
  @Operation(summary = "Rejeitar inscrição", description = "Rejeita uma inscrição pendente (apenas organizador)")
  public ResponseEntity<ApiResponse<EnrollmentResponse>> rejectEnrollment(
      @Parameter(description = "ID da inscrição") @PathVariable UUID enrollmentId,
      Authentication authentication) {
    try {
      String userEmail = authentication.getName();
      EnrollmentResponse enrollment = enrollmentService.rejectEnrollment(enrollmentId, userEmail);

      return ResponseEntity.ok(ApiResponse.success("Inscrição rejeitada", enrollment));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("Erro ao rejeitar inscrição: " + e.getMessage()));
    }
  }
}