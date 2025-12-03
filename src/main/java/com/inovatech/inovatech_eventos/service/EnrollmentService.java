package com.inovatech.inovatech_eventos.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inovatech.inovatech_eventos.model.Enrollment;
import com.inovatech.inovatech_eventos.model.EnrollmentStatus;
import com.inovatech.inovatech_eventos.model.Event;
import com.inovatech.inovatech_eventos.model.User;
import com.inovatech.inovatech_eventos.repository.EnrollmentRepository;
import com.inovatech.inovatech_eventos.repository.EventRepository;
import com.inovatech.inovatech_eventos.repository.UserRepository;

@Service
@Transactional
public class EnrollmentService {

  @Autowired
  private EnrollmentRepository enrollmentRepository;

  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EventService eventService;

  // ==================== CRUD BÁSICO ====================

  /**
   * Criar nova inscrição com validações
   */
  public Enrollment createEnrollment(UUID userId, UUID eventId) {
    // Validar se usuário existe
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    // Validar se evento existe
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

    // Validar se evento está ativo
    if (!event.isActive()) {
      throw new RuntimeException("Evento não está ativo");
    }

    // Verificar se usuário já está inscrito
    Optional<Enrollment> existingEnrollment = enrollmentRepository.findByUserIdAndEventId(userId, eventId);
    if (existingEnrollment.isPresent() && existingEnrollment.get().getStatus() != EnrollmentStatus.CANCELED) {
      throw new RuntimeException("Usuário já está inscrito neste evento");
    }

    // Verificar se há vagas disponíveis
    if (!eventService.hasAvailableSpots(eventId)) {
      throw new RuntimeException("Evento lotado - sem vagas disponíveis");
    }

    Enrollment enrollment;
    if (existingEnrollment.isPresent()) {
      // Reativar inscrição cancelada
      enrollment = existingEnrollment.get();
      enrollment.setStatus(EnrollmentStatus.CONFIRMED);
      enrollment.setEnrollmentDateTime(LocalDateTime.now());
    } else {
      // Criar nova inscrição
      enrollment = new Enrollment();
      enrollment.setUser(user);
      enrollment.setEvent(event);
      enrollment.setStatus(EnrollmentStatus.CONFIRMED); // Confirma automaticamente
      enrollment.setEnrollmentDateTime(LocalDateTime.now());
    }

    // Salvar inscrição
    return enrollmentRepository.save(enrollment);
  }

  /**
   * Buscar inscrição por ID
   */
  @Transactional(readOnly = true)
  public Optional<Enrollment> findById(UUID id) {
    return enrollmentRepository.findById(id);
  }

  /**
   * Listar inscrições por usuário
   */
  @Transactional(readOnly = true)
  public List<Enrollment> findEnrollmentsByUser(UUID userId) {
    return enrollmentRepository.findByUserId(userId);
  }

  /**
   * Listar inscrições por evento
   */
  @Transactional(readOnly = true)
  public List<Enrollment> findEnrollmentsByEvent(UUID eventId) {
    return enrollmentRepository.findByEventId(eventId);
  }

  /**
   * Cancelar inscrição
   */
  public void cancelEnrollment(UUID enrollmentId) {
    Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
        .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));

    enrollment.setStatus(EnrollmentStatus.CANCELED);
    enrollmentRepository.save(enrollment);
  }

  /**
   * Cancelar inscrição por usuário e evento
   */
  public void cancelEnrollmentByUserAndEvent(UUID userId, UUID eventId) {
    Enrollment enrollment = enrollmentRepository.findByUserIdAndEventId(userId, eventId)
        .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));

    cancelEnrollment(enrollment.getId());
  }

  /**
   * Deletar inscrição permanentemente (apenas para administradores)
   */
  public void deleteEnrollment(UUID enrollmentId) {
    if (!enrollmentRepository.existsById(enrollmentId)) {
      throw new RuntimeException("Inscrição não encontrada");
    }
    enrollmentRepository.deleteById(enrollmentId);
  }

  // ==================== MÉTODOS ESPECÍFICOS ====================

  /**
   * Listar participantes confirmados de um evento
   */
  @Transactional(readOnly = true)
  public List<Enrollment> findConfirmedParticipants(UUID eventId) {
    return enrollmentRepository.findEnrollmentsWithUserByEventIdAndStatus(
        eventId, EnrollmentStatus.CONFIRMED);
  }

  /**
   * Listar inscrições por status
   */
  @Transactional(readOnly = true)
  public List<Enrollment> findEnrollmentsByEventAndStatus(UUID eventId, EnrollmentStatus status) {
    return enrollmentRepository.findByEventIdAndStatus(eventId, status);
  }

  /**
   * Verificar se usuário está inscrito em evento
   */
  @Transactional(readOnly = true)
  public boolean isUserEnrolled(UUID userId, UUID eventId) {
    return enrollmentRepository.existsByUserIdAndEventId(userId, eventId);
  }

  /**
   * Buscar inscrição específica de usuário em evento
   */
  @Transactional(readOnly = true)
  public Optional<Enrollment> findUserEnrollmentInEvent(UUID userId, UUID eventId) {
    return enrollmentRepository.findByUserIdAndEventId(userId, eventId);
  }

  /**
   * Listar eventos confirmados de um usuário
   */
  @Transactional(readOnly = true)
  public List<Enrollment> findUserConfirmedEnrollments(UUID userId) {
    return enrollmentRepository.findByUserIdAndStatus(userId, EnrollmentStatus.CONFIRMED);
  }

  /**
   * Confirmar inscrição pendente
   */
  public Enrollment confirmEnrollment(UUID enrollmentId) {
    Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
        .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));

    // Verificar se ainda há vagas
    if (!eventService.hasAvailableSpots(enrollment.getEvent().getId())) {
      throw new RuntimeException("Evento lotado - não é possível confirmar inscrição");
    }

    enrollment.setStatus(EnrollmentStatus.CONFIRMED);
    return enrollmentRepository.save(enrollment);
  }

  /**
   * Colocar inscrição em lista de espera
   */
  public Enrollment putOnWaitingList(UUID userId, UUID eventId) {
    // Validar se usuário existe
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    // Validar se evento existe
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

    // Verificar se usuário já está inscrito
    if (enrollmentRepository.existsByUserIdAndEventId(userId, eventId)) {
      throw new RuntimeException("Usuário já está inscrito neste evento");
    }

    // Criar inscrição pendente
    Enrollment enrollment = new Enrollment();
    enrollment.setUser(user);
    enrollment.setEvent(event);
    enrollment.setStatus(EnrollmentStatus.PENDING);
    enrollment.setEnrollmentDateTime(LocalDateTime.now());

    return enrollmentRepository.save(enrollment);
  }

  /**
   * Contar inscrições por status
   */
  @Transactional(readOnly = true)
  public int countEnrollmentsByStatus(UUID eventId, EnrollmentStatus status) {
    return enrollmentRepository.countByEventIdAndStatus(eventId, status);
  }

  /**
   * Processar lista de espera quando uma vaga é liberada
   */
  public void processWaitingList(UUID eventId) {
    // Buscar primeira inscrição pendente
    List<Enrollment> pendingEnrollments = enrollmentRepository.findByEventIdAndStatus(
        eventId, EnrollmentStatus.PENDING);

    if (!pendingEnrollments.isEmpty() && eventService.hasAvailableSpots(eventId)) {
      // Confirmar primeira inscrição pendente
      Enrollment firstPending = pendingEnrollments.get(0);
      confirmEnrollment(firstPending.getId());
    }
  }

  /**
   * Obter estatísticas de inscrições do evento
   */
  @Transactional(readOnly = true)
  public EnrollmentStats getEnrollmentStats(UUID eventId) {
    int confirmed = enrollmentRepository.countByEventIdAndStatus(eventId, EnrollmentStatus.CONFIRMED);
    int pending = enrollmentRepository.countByEventIdAndStatus(eventId, EnrollmentStatus.PENDING);
    int cancelled = enrollmentRepository.countByEventIdAndStatus(eventId, EnrollmentStatus.CANCELED);
    int total = enrollmentRepository.countByEventId(eventId);

    return new EnrollmentStats(confirmed, pending, cancelled, total);
  }

  // Classe interna para estatísticas de inscrições
  public static class EnrollmentStats {
    private final int confirmed;
    private final int pending;
    private final int cancelled;
    private final int total;

    public EnrollmentStats(int confirmed, int pending, int cancelled, int total) {
      this.confirmed = confirmed;
      this.pending = pending;
      this.cancelled = cancelled;
      this.total = total;
    }

    // Getters
    public int getConfirmed() {
      return confirmed;
    }

    public int getPending() {
      return pending;
    }

    public int getCancelled() {
      return cancelled;
    }

    public int getTotal() {
      return total;
    }
  }

  // ==================== MÉTODOS PARA O CONTROLLER ====================

  /**
   * Criar inscrição através do DTO
   */
  public com.inovatech.inovatech_eventos.dto.EnrollmentResponse createEnrollment(
      com.inovatech.inovatech_eventos.dto.EnrollmentCreateRequest request, String userEmail) {

    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    Enrollment enrollment = createEnrollment(user.getId(), request.eventId());
    return mapToEnrollmentResponse(enrollment);
  }

  /**
   * Listar inscrições do usuário
   */
  public org.springframework.data.domain.Page<com.inovatech.inovatech_eventos.dto.EnrollmentResponse> getEnrollmentsByUser(
      String userEmail, org.springframework.data.domain.Pageable pageable) {

    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    return enrollmentRepository.findByUserId(user.getId(), pageable)
        .map(this::mapToEnrollmentResponse);
  }

  /**
   * Listar inscrições do evento (apenas organizador)
   */
  public org.springframework.data.domain.Page<com.inovatech.inovatech_eventos.dto.EnrollmentResponse> getEnrollmentsByEvent(
      UUID eventId, String userEmail, org.springframework.data.domain.Pageable pageable) {

    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    // Verificar se é o organizador
    if (!event.getOrganizer().getId().equals(user.getId())) {
      throw new RuntimeException("Apenas o organizador pode ver as inscrições");
    }

    return enrollmentRepository.findByEventId(eventId, pageable)
        .map(this::mapToEnrollmentResponse);
  }

  /**
   * Cancelar inscrição verificando usuário
   */
  public void cancelEnrollment(UUID enrollmentId, String userEmail) {
    Enrollment enrollment = findById(enrollmentId)
        .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    if (!enrollment.getUser().getId().equals(user.getId())) {
      throw new RuntimeException("Você só pode cancelar suas próprias inscrições");
    }

    cancelEnrollment(enrollmentId);
  }

  /**
   * Confirmar inscrição (organizador)
   */
  public com.inovatech.inovatech_eventos.dto.EnrollmentResponse confirmEnrollment(UUID enrollmentId, String userEmail) {
    Enrollment enrollment = findById(enrollmentId)
        .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    if (!enrollment.getEvent().getOrganizer().getId().equals(user.getId())) {
      throw new RuntimeException("Apenas o organizador pode confirmar inscrições");
    }

    Enrollment confirmed = confirmEnrollment(enrollmentId);
    return mapToEnrollmentResponse(confirmed);
  }

  /**
   * Rejeitar inscrição (organizador)
   */
  public com.inovatech.inovatech_eventos.dto.EnrollmentResponse rejectEnrollment(UUID enrollmentId, String userEmail) {
    Enrollment enrollment = findById(enrollmentId)
        .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    if (!enrollment.getEvent().getOrganizer().getId().equals(user.getId())) {
      throw new RuntimeException("Apenas o organizador pode rejeitar inscrições");
    }

    // Criar método específico para rejeitar
    enrollment.setStatus(EnrollmentStatus.CANCELED);
    Enrollment rejected = enrollmentRepository.save(enrollment);
    return mapToEnrollmentResponse(rejected);
  }

  /**
   * Mapear Enrollment para EnrollmentResponse
   */
  private com.inovatech.inovatech_eventos.dto.EnrollmentResponse mapToEnrollmentResponse(Enrollment enrollment) {
    return new com.inovatech.inovatech_eventos.dto.EnrollmentResponse(
        enrollment.getId(),
        enrollment.getEvent().getId(),
        enrollment.getEvent().getTitle(),
        enrollment.getEvent().getStartDateTime(),
        enrollment.getEvent().getLocation(),
        enrollment.getUser().getId(),
        enrollment.getUser().getName(),
        enrollment.getUser().getEmail(),
        enrollment.getStatus(),
        enrollment.getEnrollmentDateTime());
  }
}