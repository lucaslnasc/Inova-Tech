package com.inovatech.inovatech_eventos.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.inovatech.inovatech_eventos.model.Enrollment;
import com.inovatech.inovatech_eventos.model.EnrollmentStatus;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

  // Buscar inscrições por usuário
  List<Enrollment> findByUserId(UUID userId);

  // Buscar inscrições por usuário com paginação
  Page<Enrollment> findByUserId(UUID userId, Pageable pageable);

  // Buscar inscrições por evento
  List<Enrollment> findByEventId(UUID eventId);

  // Buscar inscrições por evento com paginação
  Page<Enrollment> findByEventId(UUID eventId, Pageable pageable);

  // Buscar inscrições por evento e status
  List<Enrollment> findByEventIdAndStatus(UUID eventId, EnrollmentStatus status);

  // Buscar inscrição específica de um usuário em um evento
  Optional<Enrollment> findByUserIdAndEventId(UUID userId, UUID eventId);

  // Contar inscrições por evento e status (para verificar vagas)
  int countByEventIdAndStatus(UUID eventId, EnrollmentStatus status);

  // Contar todas as inscrições por evento
  int countByEventId(UUID eventId);

  // Verificar se usuário já está inscrito no evento
  boolean existsByUserIdAndEventId(UUID userId, UUID eventId);

  // Buscar inscrições confirmadas por usuário
  List<Enrollment> findByUserIdAndStatus(UUID userId, EnrollmentStatus status);

  // Query personalizada para buscar participantes de um evento
  @Query("SELECT e FROM enrollments e JOIN FETCH e.user WHERE e.event.id = :eventId AND e.status = :status")
  List<Enrollment> findEnrollmentsWithUserByEventIdAndStatus(UUID eventId, EnrollmentStatus status);
}