package com.inovatech.inovatech_eventos.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Length(max = 100, message = "O nome deve ter no máximo 100 caracteres")
  @NotBlank(message = "O nome não pode estar em branco")
  private String name;

  @Length(max = 100, message = "O email deve ter no máximo 100 caracteres")
  @NotBlank(message = "O email não pode estar em branco")
  private String email;

  @Length(max = 100, message = "A senha deve ter no máximo 100 caracteres")
  @NotBlank(message = "A senha não pode estar em branco")
  private String password;

  @Enumerated(EnumType.STRING)
  private TypeUser type;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
