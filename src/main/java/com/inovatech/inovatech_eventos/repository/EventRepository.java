package com.inovatech.inovatech_eventos.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.inovatech.inovatech_eventos.model.Event;

public interface EventRepository extends JpaRepository<Event, UUID> {
  List<Event> findByIsActiveTrue();

  // Métodos com paginação
  Page<Event> findByIsActiveTrue(Pageable pageable);

  List<Event> findByOrganizerId(UUID organizerId);

  Page<Event> findByOrganizerId(UUID organizerId, Pageable pageable);

  List<Event> findByTitleContainingIgnoreCase(String title);

  List<Event> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);

  // Buscar eventos ativos por organizador
  List<Event> findByOrganizerIdAndIsActiveTrue(UUID organizerId);

  // Buscar eventos futuros (útil para listagem pública)
  List<Event> findByIsActiveTrueAndStartDateTimeAfter(LocalDateTime now);

  // Buscar por descrição também (busca mais ampla)
  List<Event> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

  // Busca com paginação para título e localização
  Page<Event> findByIsActiveTrueAndTitleContainingIgnoreCaseOrLocationContainingIgnoreCase(
      String title, String location, Pageable pageable);
}
