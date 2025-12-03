package com.inovatech.inovatech_eventos.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inovatech.inovatech_eventos.model.TypeUser;
import com.inovatech.inovatech_eventos.model.User;
import com.inovatech.inovatech_eventos.repository.UserRepository;

@Service
@Transactional
public class UserService {

  @Autowired
  private UserRepository userRepository;

  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  // ==================== CRUD BÁSICO ====================

  /**
   * Criar novo usuário com validações
   */
  public User createUser(User user) {
    // Validar se email já existe
    if (userRepository.existsByEmail(user.getEmail())) {
      throw new RuntimeException("Email já cadastrado no sistema");
    }

    // Hash da senha antes de salvar
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    return userRepository.save(user);
  }

  /**
   * Buscar usuário por ID
   */
  @Transactional(readOnly = true)
  public Optional<User> findById(UUID id) {
    return userRepository.findById(id);
  }

  /**
   * Buscar usuário por email
   */
  @Transactional(readOnly = true)
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  /**
   * Listar todos os usuários
   */
  @Transactional(readOnly = true)
  public List<User> findAllUsers() {
    return userRepository.findAll();
  }

  /**
   * Atualizar usuário
   */
  public User updateUser(UUID id, User updatedUser) {
    User existingUser = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    // Verificar se o email foi alterado e se já existe
    if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
      if (userRepository.existsByEmail(updatedUser.getEmail())) {
        throw new RuntimeException("Email já cadastrado no sistema");
      }
    }

    // Atualizar campos
    existingUser.setName(updatedUser.getName());
    existingUser.setEmail(updatedUser.getEmail());
    existingUser.setType(updatedUser.getType());

    // Só atualizar senha se foi fornecida uma nova
    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
      existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
    }

    return userRepository.save(existingUser);
  }

  /**
   * Deletar usuário
   */
  public void deleteUser(UUID id) {
    if (!userRepository.existsById(id)) {
      throw new RuntimeException("Usuário não encontrado");
    }
    userRepository.deleteById(id);
  }

  // ==================== MÉTODOS ESPECÍFICOS ====================

  /**
   * Validar senha (para login)
   */
  @Transactional(readOnly = true)
  public boolean validatePassword(String rawPassword, String hashedPassword) {
    return passwordEncoder.matches(rawPassword, hashedPassword);
  }

  /**
   * Buscar usuários por tipo (ORGANIZADOR ou PARTICIPANTE)
   */
  @Transactional(readOnly = true)
  public List<User> findUsersByType(TypeUser type) {
    return userRepository.findByType(type);
  }

  /**
   * Buscar usuários por nome (case-insensitive)
   */
  @Transactional(readOnly = true)
  public List<User> searchUsersByName(String name) {
    return userRepository.findByNameContainingIgnoreCase(name);
  }

  /**
   * Verificar se email já existe (para validação frontend)
   */
  @Transactional(readOnly = true)
  public boolean isEmailTaken(String email) {
    return userRepository.existsByEmail(email);
  }

  /**
   * Autenticar usuário (para login)
   */
  @Transactional(readOnly = true)
  public Optional<User> authenticateUser(String email, String password) {
    Optional<User> userOpt = userRepository.findByEmail(email);

    if (userOpt.isPresent()) {
      User user = userOpt.get();
      if (validatePassword(password, user.getPassword())) {
        return Optional.of(user);
      }
    }

    return Optional.empty();
  }

  // ==================== MÉTODOS COM DTOs ====================

  /**
   * Criar usuário através do DTO
   */
  public com.inovatech.inovatech_eventos.dto.UserResponse createUser(
      com.inovatech.inovatech_eventos.dto.UserCreateRequest request) {

    User user = User.builder()
        .name(request.name())
        .email(request.email())
        .password(request.password())
        .type(request.type())
        .build();

    User createdUser = createUser(user);
    return com.inovatech.inovatech_eventos.dto.UserResponse.from(createdUser);
  }

  /**
   * Atualizar usuário através do DTO
   */
  public com.inovatech.inovatech_eventos.dto.UserResponse updateUser(
      UUID id, com.inovatech.inovatech_eventos.dto.UserUpdateRequest request) {

    User updateData = User.builder()
        .name(request.name())
        .email(request.email())
        .password(request.password())
        .type(request.type())
        .build();

    User updatedUser = updateUser(id, updateData);
    return com.inovatech.inovatech_eventos.dto.UserResponse.from(updatedUser);
  }

  /**
   * Buscar usuário por ID retornando DTO
   */
  public com.inovatech.inovatech_eventos.dto.UserResponse getUserById(UUID id) {
    User user = findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    return com.inovatech.inovatech_eventos.dto.UserResponse.from(user);
  }

  /**
   * Buscar usuário por email retornando DTO
   */
  public com.inovatech.inovatech_eventos.dto.UserResponse getUserByEmail(String email) {
    User user = findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    return com.inovatech.inovatech_eventos.dto.UserResponse.from(user);
  }

  /**
   * Listar todos os usuários retornando DTOs
   */
  public java.util.List<com.inovatech.inovatech_eventos.dto.UserResponse> getAllUsers() {
    return findAllUsers().stream()
        .map(com.inovatech.inovatech_eventos.dto.UserResponse::from)
        .toList();
  }

  /**
   * Buscar usuários por tipo retornando DTOs
   */
  public java.util.List<com.inovatech.inovatech_eventos.dto.UserResponse> getUsersByType(TypeUser type) {
    return findUsersByType(type).stream()
        .map(com.inovatech.inovatech_eventos.dto.UserResponse::from)
        .toList();
  }

  /**
   * Buscar usuários por nome retornando DTOs
   */
  public java.util.List<com.inovatech.inovatech_eventos.dto.UserResponse> searchUsersByNameDto(String name) {
    return searchUsersByName(name).stream()
        .map(com.inovatech.inovatech_eventos.dto.UserResponse::from)
        .toList();
  }

  /**
   * Login com DTO
   */
  public com.inovatech.inovatech_eventos.dto.LoginResponse login(
      com.inovatech.inovatech_eventos.dto.LoginRequest request,
      com.inovatech.inovatech_eventos.security.JWTProvider jwtProvider) {

    Optional<User> userOpt = authenticateUser(request.email(), request.password());

    if (userOpt.isEmpty()) {
      throw new RuntimeException("Email ou senha inválidos");
    }

    User user = userOpt.get();
    String token = jwtProvider.generateToken(user);
    com.inovatech.inovatech_eventos.dto.UserResponse userResponse = com.inovatech.inovatech_eventos.dto.UserResponse
        .from(user);

    return new com.inovatech.inovatech_eventos.dto.LoginResponse(
        userResponse, token, "Login realizado com sucesso");
  }
}