package com.inovatech.inovatech_eventos.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "enrollments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "event_id", nullable = false)
  @NotNull(message = "O evento é obrigatório")
  private Event event;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  @NotNull(message = "O usuário é obrigatório")
  private User user;

  @CreationTimestamp
  private LocalDateTime enrollmentDateTime;

  @Enumerated(EnumType.STRING)
  @NotNull(message = "O status da inscrição é obrigatório")
  private EnrollmentStatus status;
}
