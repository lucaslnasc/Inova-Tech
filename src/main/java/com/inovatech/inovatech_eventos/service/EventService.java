package com.inovatech.inovatech_eventos.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inovatech.inovatech_eventos.model.Event;
import com.inovatech.inovatech_eventos.model.User;
import com.inovatech.inovatech_eventos.repository.EnrollmentRepository;
import com.inovatech.inovatech_eventos.repository.EventRepository;
import com.inovatech.inovatech_eventos.repository.UserRepository;

@Service
@Transactional
public class EventService {

  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EnrollmentRepository enrollmentRepository;

  // ==================== CRUD BÁSICO ====================

  /**
   * Criar novo evento com validações
   */
  public Event createEvent(Event event, UUID organizerId) {
    // Validar se organizador existe
    User organizer = userRepository.findById(organizerId)
        .orElseThrow(() -> new RuntimeException("Organizador não encontrado"));

    // Validar se data de início é anterior à data de fim
    if (event.getStartDateTime().isAfter(event.getEndDateTime())) {
      throw new RuntimeException("Data de início não pode ser posterior à data de fim");
    }

    // Associar organizador ao evento
    event.setOrganizer(organizer);

    // Definir como ativo por padrão
    event.setActive(true);

    return eventRepository.save(event);
  }

  /**
   * Buscar evento por ID
   */
  @Transactional(readOnly = true)
  public Optional<Event> findById(UUID id) {
    return eventRepository.findById(id);
  }

  /**
   * Listar todos os eventos ativos
   */
  @Transactional(readOnly = true)
  public List<Event> findAllActiveEvents() {
    return eventRepository.findByIsActiveTrue();
  }

  /**
   * Listar eventos por organizador
   */
  @Transactional(readOnly = true)
  public List<Event> findEventsByOrganizer(UUID organizerId) {
    return eventRepository.findByOrganizerId(organizerId);
  }

  /**
   * Atualizar evento
   */
  public Event updateEvent(UUID id, Event updatedEvent) {
    Event existingEvent = eventRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

    // Validar se data de início é anterior à data de fim
    if (updatedEvent.getStartDateTime().isAfter(updatedEvent.getEndDateTime())) {
      throw new RuntimeException("Data de início não pode ser posterior à data de fim");
    }

    // Atualizar campos
    existingEvent.setTitle(updatedEvent.getTitle());
    existingEvent.setDescription(updatedEvent.getDescription());
    existingEvent.setStartDateTime(updatedEvent.getStartDateTime());
    existingEvent.setEndDateTime(updatedEvent.getEndDateTime());
    existingEvent.setLocation(updatedEvent.getLocation());
    existingEvent.setCapacity(updatedEvent.getCapacity());
    existingEvent.setActive(updatedEvent.isActive());

    return eventRepository.save(existingEvent);
  }

  /**
   * Deletar evento (inativar ao invés de deletar se houver inscrições)
   */
  public void deleteEvent(UUID id) {
    Event event = eventRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

    // Verificar se há inscrições
    int enrollmentCount = enrollmentRepository.countByEventId(id);

    if (enrollmentCount > 0) {
      // Inativar ao invés de deletar se houver inscrições
      event.setActive(false);
      eventRepository.save(event);
    } else {
      // Deletar se não houver inscrições
      eventRepository.deleteById(id);
    }
  }

  // ==================== MÉTODOS ESPECÍFICOS ====================

  /**
   * Buscar eventos por palavra-chave (título)
   */
  @Transactional(readOnly = true)
  public List<Event> searchEventsByTitle(String keyword) {
    return eventRepository.findByTitleContainingIgnoreCase(keyword);
  }

  /**
   * Buscar eventos por palavra-chave (título ou descrição)
   */
  @Transactional(readOnly = true)
  public List<Event> searchEvents(String keyword) {
    return eventRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
  }

  /**
   * Verificar se evento tem vagas disponíveis
   */
  @Transactional(readOnly = true)
  public boolean hasAvailableSpots(UUID eventId) {
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

    int confirmedEnrollments = enrollmentRepository.countByEventIdAndStatus(
        eventId, com.inovatech.inovatech_eventos.model.EnrollmentStatus.CONFIRMED);

    return confirmedEnrollments < event.getCapacity();
  }

  /**
   * Obter número de vagas disponíveis
   */
  @Transactional(readOnly = true)
  public int getAvailableSpots(UUID eventId) {
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

    int confirmedEnrollments = enrollmentRepository.countByEventIdAndStatus(
        eventId, com.inovatech.inovatech_eventos.model.EnrollmentStatus.CONFIRMED);

    return Math.max(0, event.getCapacity() - confirmedEnrollments);
  }

  /**
   * Buscar eventos por intervalo de datas
   */
  @Transactional(readOnly = true)
  public List<Event> findEventsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    return eventRepository.findByStartDateTimeBetween(startDate, endDate);
  }

  /**
   * Buscar eventos futuros (a partir de agora)
   */
  @Transactional(readOnly = true)
  public List<Event> findUpcomingEvents() {
    return eventRepository.findByIsActiveTrueAndStartDateTimeAfter(LocalDateTime.now());
  }

  /**
   * Buscar eventos ativos por organizador
   */
  @Transactional(readOnly = true)
  public List<Event> findActiveEventsByOrganizer(UUID organizerId) {
    return eventRepository.findByOrganizerIdAndIsActiveTrue(organizerId);
  }

  /**
   * Verificar se usuário é organizador do evento
   */
  @Transactional(readOnly = true)
  public boolean isEventOrganizer(UUID eventId, UUID userId) {
    Optional<Event> eventOpt = eventRepository.findById(eventId);
    if (eventOpt.isPresent()) {
      return eventOpt.get().getOrganizer().getId().equals(userId);
    }
    return false;
  }

  /**
   * Obter estatísticas básicas do evento
   */
  @Transactional(readOnly = true)
  public EventStats getEventStats(UUID eventId) {
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

    int confirmedEnrollments = enrollmentRepository.countByEventIdAndStatus(
        eventId, com.inovatech.inovatech_eventos.model.EnrollmentStatus.CONFIRMED);

    int pendingEnrollments = enrollmentRepository.countByEventIdAndStatus(
        eventId, com.inovatech.inovatech_eventos.model.EnrollmentStatus.PENDING);

    return new EventStats(
        event.getCapacity(),
        confirmedEnrollments,
        pendingEnrollments,
        event.getCapacity() - confirmedEnrollments);
  }

  // Classe interna para estatísticas do evento
  public static class EventStats {
    private final int totalCapacity;
    private final int confirmedEnrollments;
    private final int pendingEnrollments;
    private final int availableSpots;

    public EventStats(int totalCapacity, int confirmedEnrollments, int pendingEnrollments, int availableSpots) {
      this.totalCapacity = totalCapacity;
      this.confirmedEnrollments = confirmedEnrollments;
      this.pendingEnrollments = pendingEnrollments;
      this.availableSpots = availableSpots;
    }

    // Getters
    public int getTotalCapacity() {
      return totalCapacity;
    }

    public int getConfirmedEnrollments() {
      return confirmedEnrollments;
    }

    public int getPendingEnrollments() {
      return pendingEnrollments;
    }

    public int getAvailableSpots() {
      return availableSpots;
    }
  }

  // ==================== MÉTODOS PARA O CONTROLLER ====================

  /**
   * Criar evento através do DTO
   */
  public com.inovatech.inovatech_eventos.dto.EventResponse createEvent(
      com.inovatech.inovatech_eventos.dto.EventCreateRequest request, String userEmail) {

    User organizer = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    Event event = Event.builder()
        .title(request.title())
        .description(request.description())
        .startDateTime(request.startDateTime())
        .endDateTime(request.endDateTime())
        .location(request.location())
        .capacity(request.capacity())
        .organizer(organizer)
        .isActive(true)
        .build();

    Event saved = createEvent(event, organizer.getId());
    return mapToEventResponse(saved);
  }

  /**
   * Buscar evento por ID retornando DTO
   */
  public com.inovatech.inovatech_eventos.dto.EventResponse getEventById(UUID id) {
    Event event = findById(id).orElseThrow(() -> new RuntimeException("Evento não encontrado"));
    return mapToEventResponse(event);
  }

  /**
   * Atualizar evento através do DTO
   */
  public com.inovatech.inovatech_eventos.dto.EventResponse updateEvent(
      UUID id, com.inovatech.inovatech_eventos.dto.EventUpdateRequest request, String userEmail) {

    Event event = findById(id).orElseThrow(() -> new RuntimeException("Evento não encontrado"));
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    // Verificar se o usuário é o organizador
    if (!event.getOrganizer().getId().equals(user.getId())) {
      throw new RuntimeException("Apenas o organizador pode atualizar o evento");
    }

    // Atualizar campos não nulos
    if (request.title() != null)
      event.setTitle(request.title());
    if (request.description() != null)
      event.setDescription(request.description());
    if (request.startDateTime() != null)
      event.setStartDateTime(request.startDateTime());
    if (request.endDateTime() != null)
      event.setEndDateTime(request.endDateTime());
    if (request.location() != null)
      event.setLocation(request.location());
    if (request.capacity() != null)
      event.setCapacity(request.capacity());
    if (request.isActive() != null)
      event.setActive(request.isActive());

    Event updated = updateEvent(id, event);
    return mapToEventResponse(updated);
  }

  /**
   * Deletar evento verificando organizador
   */
  public void deleteEvent(UUID id, String userEmail) {
    Event event = findById(id).orElseThrow(() -> new RuntimeException("Evento não encontrado"));
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    if (!event.getOrganizer().getId().equals(user.getId())) {
      throw new RuntimeException("Apenas o organizador pode deletar o evento");
    }

    deleteEvent(id);
  }

  /**
   * Listar eventos ativos com paginação
   */
  public org.springframework.data.domain.Page<com.inovatech.inovatech_eventos.dto.EventResponse> getAllActiveEvents(
      org.springframework.data.domain.Pageable pageable) {

    return eventRepository.findByIsActiveTrue(pageable)
        .map(this::mapToEventResponse);
  }

  /**
   * Buscar eventos por termo
   */
  public org.springframework.data.domain.Page<com.inovatech.inovatech_eventos.dto.EventResponse> searchEvents(
      String query, org.springframework.data.domain.Pageable pageable) {

    return eventRepository.findByIsActiveTrueAndTitleContainingIgnoreCaseOrLocationContainingIgnoreCase(
        query, query, pageable).map(this::mapToEventResponse);
  }

  /**
   * Listar eventos do organizador
   */
  public org.springframework.data.domain.Page<com.inovatech.inovatech_eventos.dto.EventResponse> getEventsByOrganizer(
      String userEmail, org.springframework.data.domain.Pageable pageable) {

    User organizer = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    return eventRepository.findByOrganizerId(organizer.getId(), pageable)
        .map(this::mapToEventResponse);
  }

  /**
   * Mapear Event para EventResponse
   */
  private com.inovatech.inovatech_eventos.dto.EventResponse mapToEventResponse(Event event) {
    int currentEnrollments = enrollmentRepository.countByEventIdAndStatus(
        event.getId(), com.inovatech.inovatech_eventos.model.EnrollmentStatus.CONFIRMED);

    return new com.inovatech.inovatech_eventos.dto.EventResponse(
        event.getId(),
        event.getTitle(),
        event.getDescription(),
        event.getStartDateTime(),
        event.getEndDateTime(),
        event.getLocation(),
        event.getCapacity(),
        currentEnrollments,
        event.isActive(),
        event.getOrganizer().getId(),
        event.getOrganizer().getName(),
        event.getOrganizer().getEmail());
  }
}