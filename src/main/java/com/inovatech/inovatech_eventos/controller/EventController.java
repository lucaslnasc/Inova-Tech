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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inovatech.inovatech_eventos.dto.ApiResponse;
import com.inovatech.inovatech_eventos.dto.EventCreateRequest;
import com.inovatech.inovatech_eventos.dto.EventResponse;
import com.inovatech.inovatech_eventos.dto.EventUpdateRequest;
import com.inovatech.inovatech_eventos.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST para gerenciamento de eventos
 * Versão: 1.0
 * 
 * @author InovaTech Team
 */
@RestController
@RequestMapping("/api/v1/events")
@Tag(name = "Events", description = "API de gerenciamento de eventos")
@SecurityRequirement(name = "bearerAuth")
public class EventController {

  @Autowired
  private EventService eventService;

  /**
   * Criar novo evento
   */
  @PostMapping
  @Operation(summary = "Criar evento", description = "Cria um novo evento (apenas organizadores)")
  public ResponseEntity<ApiResponse<EventResponse>> createEvent(
      @Valid @RequestBody EventCreateRequest request,
      Authentication authentication) {
    try {
      String userEmail = authentication.getName();
      EventResponse event = eventService.createEvent(request, userEmail);

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(ApiResponse.success("Evento criado com sucesso", event));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("Erro ao criar evento: " + e.getMessage()));
    }
  }

  /**
   * Listar todos os eventos com paginação
   */
  @GetMapping
  @Operation(summary = "Listar eventos", description = "Lista todos os eventos ativos com paginação")
  public ResponseEntity<ApiResponse<Page<EventResponse>>> getAllEvents(
      @Parameter(description = "Parâmetros de paginação") Pageable pageable) {
    try {
      Page<EventResponse> events = eventService.getAllActiveEvents(pageable);
      return ResponseEntity.ok(ApiResponse.success("Eventos listados com sucesso", events));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("Erro ao listar eventos: " + e.getMessage()));
    }
  }

  /**
   * Buscar evento por ID
   */
  @GetMapping("/{id}")
  @Operation(summary = "Buscar evento", description = "Busca um evento específico por ID")
  public ResponseEntity<ApiResponse<EventResponse>> getEventById(
      @Parameter(description = "ID do evento") @PathVariable UUID id) {
    try {
      EventResponse event = eventService.getEventById(id);
      return ResponseEntity.ok(ApiResponse.success("Evento encontrado", event));
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Atualizar evento
   */
  @PutMapping("/{id}")
  @Operation(summary = "Atualizar evento", description = "Atualiza um evento existente (apenas o organizador)")
  public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
      @Parameter(description = "ID do evento") @PathVariable UUID id,
      @Valid @RequestBody EventUpdateRequest request,
      Authentication authentication) {
    try {
      String userEmail = authentication.getName();
      EventResponse event = eventService.updateEvent(id, request, userEmail);

      return ResponseEntity.ok(ApiResponse.success("Evento atualizado com sucesso", event));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("Erro ao atualizar evento: " + e.getMessage()));
    }
  }

  /**
   * Deletar evento
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Deletar evento", description = "Remove um evento (apenas o organizador)")
  public ResponseEntity<ApiResponse<Void>> deleteEvent(
      @Parameter(description = "ID do evento") @PathVariable UUID id,
      Authentication authentication) {
    try {
      String userEmail = authentication.getName();
      eventService.deleteEvent(id, userEmail);

      return ResponseEntity.ok(ApiResponse.<Void>success("Evento removido com sucesso", null));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("Erro ao remover evento: " + e.getMessage()));
    }
  }

  /**
   * Buscar eventos por título ou localização
   */
  @GetMapping("/search")
  @Operation(summary = "Buscar eventos", description = "Busca eventos por título ou localização")
  public ResponseEntity<ApiResponse<Page<EventResponse>>> searchEvents(
      @Parameter(description = "Termo de busca") @RequestParam String query,
      @Parameter(description = "Parâmetros de paginação") Pageable pageable) {
    try {
      Page<EventResponse> events = eventService.searchEvents(query, pageable);
      return ResponseEntity.ok(ApiResponse.success("Busca realizada com sucesso", events));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("Erro na busca: " + e.getMessage()));
    }
  }

  /**
   * Listar eventos do usuário organizador
   */
  @GetMapping("/my-events")
  @Operation(summary = "Meus eventos", description = "Lista eventos criados pelo usuário autenticado")
  public ResponseEntity<ApiResponse<Page<EventResponse>>> getMyEvents(
      Authentication authentication,
      @Parameter(description = "Parâmetros de paginação") Pageable pageable) {
    try {
      String userEmail = authentication.getName();
      Page<EventResponse> events = eventService.getEventsByOrganizer(userEmail, pageable);
      return ResponseEntity.ok(ApiResponse.success("Eventos do usuário listados", events));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("Erro ao listar eventos: " + e.getMessage()));
    }
  }
}