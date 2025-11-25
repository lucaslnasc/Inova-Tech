package com.inovatech.inovatech_eventos.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inovatech.inovatech_eventos.dto.ApiResponse;
import com.inovatech.inovatech_eventos.dto.LoginRequest;
import com.inovatech.inovatech_eventos.dto.LoginResponse;
import com.inovatech.inovatech_eventos.dto.UserCreateRequest;
import com.inovatech.inovatech_eventos.dto.UserResponse;
import com.inovatech.inovatech_eventos.dto.UserUpdateRequest;
import com.inovatech.inovatech_eventos.model.TypeUser;
import com.inovatech.inovatech_eventos.model.User;
import com.inovatech.inovatech_eventos.security.JWTProvider;
import com.inovatech.inovatech_eventos.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST para gerenciamento de usuários
 * Versão: 1.0
 * 
 * @author InovaTech Team
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints para gerenciamento de usuários")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private JWTProvider jwtProvider; // ==================== CRUD BÁSICO ====================

  /**
   * Criar novo usuário
   */
  @PostMapping
  @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos ou email já existe"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro interno do servidor")
  })
  public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
    try {
      UserResponse userResponse = userService.createUser(request);

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(ApiResponse.success("Usuário criado com sucesso", userResponse));

    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ApiResponse.error(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Erro interno do servidor"));
    }
  }

  /**
   * Buscar usuário por ID
   */
  @GetMapping("/{id}")
  @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados de um usuário específico")
  public ResponseEntity<ApiResponse<UserResponse>> getUserById(
      @Parameter(description = "ID do usuário") @PathVariable UUID id) {
    try {
      UserResponse userResponse = userService.getUserById(id);
      return ResponseEntity.ok(ApiResponse.success("Usuário encontrado", userResponse));

    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Erro interno do servidor"));
    }
  }

  /**
   * Buscar usuário por email
   */
  @GetMapping("/email/{email}")
  @Operation(summary = "Buscar usuário por email", description = "Retorna os dados de um usuário pelo email")
  public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(
      @Parameter(description = "Email do usuário") @PathVariable String email) {
    try {
      UserResponse userResponse = userService.getUserByEmail(email);
      return ResponseEntity.ok(ApiResponse.success("Usuário encontrado", userResponse));

    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Erro interno do servidor"));
    }
  }

  /**
   * Listar todos os usuários
   */
  @GetMapping
  @Operation(summary = "Listar usuários", description = "Lista todos os usuários do sistema")
  public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
    try {
      List<UserResponse> userResponses = userService.getAllUsers();
      return ResponseEntity.ok(ApiResponse.success("Lista de usuários", userResponses));

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Erro interno do servidor"));
    }
  }

  /**
   * Atualizar usuário
   */
  @PutMapping("/{id}")
  @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
  public ResponseEntity<ApiResponse<UserResponse>> updateUser(
      @Parameter(description = "ID do usuário") @PathVariable UUID id,
      @Valid @RequestBody UserUpdateRequest request) {
    try {
      UserResponse userResponse = userService.updateUser(id, request);
      return ResponseEntity.ok(ApiResponse.success("Usuário atualizado com sucesso", userResponse));

    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ApiResponse.error(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Erro interno do servidor"));
    }
  }

  /**
   * Deletar usuário
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema")
  public ResponseEntity<ApiResponse<Void>> deleteUser(
      @Parameter(description = "ID do usuário") @PathVariable UUID id) {
    try {
      userService.deleteUser(id);
      return ResponseEntity.ok(ApiResponse.success("Usuário deletado com sucesso", null));

    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Erro interno do servidor"));
    }
  }

  // ==================== ENDPOINTS ESPECÍFICOS ====================

  /**
   * Buscar usuários por tipo
   */
  @GetMapping("/type/{type}")
  @Operation(summary = "Buscar usuários por tipo", description = "Lista usuários por tipo (ORGANIZADOR ou PARTICIPANTE)")
  public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByType(
      @Parameter(description = "Tipo do usuário") @PathVariable TypeUser type) {
    try {
      List<UserResponse> userResponses = userService.getUsersByType(type);
      return ResponseEntity.ok(ApiResponse.success("Usuários encontrados", userResponses));

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Erro interno do servidor"));
    }
  }

  /**
   * Buscar usuários por nome
   */
  @GetMapping("/search")
  @Operation(summary = "Buscar usuários por nome", description = "Busca usuários que contenham o nome especificado")
  public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsersByName(
      @Parameter(description = "Nome a ser buscado") @RequestParam String name) {
    try {
      List<UserResponse> userResponses = userService.searchUsersByNameDto(name);
      return ResponseEntity.ok(ApiResponse.success("Resultados da busca", userResponses));

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Erro interno do servidor"));
    }
  }

  /**
   * Verificar se email já existe
   */
  @GetMapping("/check-email")
  @Operation(summary = "Verificar disponibilidade do email", description = "Verifica se um email já está em uso")
  public ResponseEntity<ApiResponse<Boolean>> checkEmailAvailability(
      @Parameter(description = "Email a ser verificado") @RequestParam String email) {
    try {
      boolean isEmailTaken = userService.isEmailTaken(email);
      return ResponseEntity.ok(ApiResponse.success("Verificação de email", !isEmailTaken));

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Erro interno do servidor"));
    }
  }

  // ==================== AUTENTICAÇÃO ====================

  /**
   * Login de usuário
   */
  @PostMapping("/login")
  @Operation(summary = "Fazer login", description = "Autentica um usuário no sistema")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados de login inválidos")
  })
  public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
    try {
      LoginResponse loginResponse = userService.login(request, jwtProvider);
      return ResponseEntity.ok(ApiResponse.success("Login realizado com sucesso", loginResponse));

    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Erro interno do servidor"));
    }
  }

  /**
   * Validar senha (para mudanças de senha)
   */
  @PostMapping("/{id}/validate-password")
  @Operation(summary = "Validar senha atual", description = "Valida se a senha atual está correta")
  public ResponseEntity<ApiResponse<Boolean>> validatePassword(
      @Parameter(description = "ID do usuário") @PathVariable UUID id,
      @RequestParam String password) {
    try {
      Optional<User> userOpt = userService.findById(id);

      if (userOpt.isPresent()) {
        boolean isValid = userService.validatePassword(password, userOpt.get().getPassword());
        return ResponseEntity.ok(ApiResponse.success("Validação de senha", isValid));
      } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("Usuário não encontrado"));
      }

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Erro interno do servidor"));
    }
  }
}