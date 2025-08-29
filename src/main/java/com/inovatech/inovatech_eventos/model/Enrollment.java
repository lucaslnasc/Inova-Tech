package com.inovatech.inovatech_eventos.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
  private Event event;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private LocalDateTime enrollmentDateTime;
}
