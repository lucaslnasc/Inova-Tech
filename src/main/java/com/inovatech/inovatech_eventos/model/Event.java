package com.inovatech.inovatech_eventos.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Length(max = 100, message = "O título deve ter no máximo 100 caracteres")
  @NotBlank(message = "O título não pode estar em branco")
  private String title;

  @Length(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
  @NotBlank(message = "A descrição não pode estar em branco")
  private String description;

  @NotNull(message = "A data de início é obrigatória")
  private LocalDateTime startDateTime;

  @NotNull(message = "A data de término é obrigatória")
  private LocalDateTime endDateTime;

  @Length(max = 200, message = "A localização deve ter no máximo 200 caracteres")
  @NotBlank(message = "A localização não pode estar em branco")
  private String location;

  @ManyToOne
  @JoinColumn(name = "organizer_id", nullable = false)
  @NotNull(message = "O organizador é obrigatório")
  private User organizer;

  @NotNull(message = "A capacidade não pode estar em branco")
  @Min(value = 1, message = "A capacidade deve ser pelo menos 1")
  private int capacity;

  private boolean isActive;
}
