package com.inovatech.inovatech_eventos.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inovatech.inovatech_eventos.model.TypeUser;
import com.inovatech.inovatech_eventos.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String email);

  List<User> findByType(TypeUser type);

  boolean existsByEmail(String email);

  // Buscar usuário por nome (busca parcial case-insensitive)
  List<User> findByNameContainingIgnoreCase(String name);
}
