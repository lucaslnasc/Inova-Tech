package com.inovatech.inovatech_eventos.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.inovatech.inovatech_eventos.model.User;

@Service
public class JWTProvider {

  @Value("${security.token.secret:INOVATECH_JWT_SECRET_KEY_2025_MUITO_SEGURA_AQUI}")
  private String secretKey;

  @Value("${security.token.expiration:3600000}")
  private Long expiration;

  /**
   * Gerar token JWT para usuário autenticado
   */
  public String generateToken(User user) {
    Algorithm algorithm = Algorithm.HMAC256(secretKey);

    return JWT.create()
        .withIssuer("inovatech-eventos")
        .withSubject(user.getId().toString())
        .withClaim("userId", user.getId().toString())
        .withClaim("email", user.getEmail())
        .withClaim("name", user.getName())
        .withClaim("type", user.getType().toString())
        .withExpiresAt(genExpirationDate())
        .sign(algorithm);
  }

  /**
   * Validar token JWT e retornar o userId se válido
   */
  public String validateToken(String token) {
    token = token.replace("Bearer ", "");

    Algorithm algorithm = Algorithm.HMAC256(secretKey);

    try {
      var verifier = JWT.require(algorithm)
          .withIssuer("inovatech-eventos")
          .build();

      var decodedJWT = verifier.verify(token);
      return decodedJWT.getSubject();
    } catch (JWTVerificationException ex) {
      System.err.println("Token JWT inválido: " + ex.getMessage());
      return "";
    }
  }

  /**
   * Extrair claim específico do token
   */
  public String getClaimFromToken(String token, String claimName) {
    token = token.replace("Bearer ", "");

    Algorithm algorithm = Algorithm.HMAC256(secretKey);

    try {
      var verifier = JWT.require(algorithm)
          .withIssuer("inovatech-eventos")
          .build();

      var decodedJWT = verifier.verify(token);
      return decodedJWT.getClaim(claimName).asString();
    } catch (JWTVerificationException ex) {
      System.err.println("Erro ao extrair claim do token: " + ex.getMessage());
      return "";
    }
  }

  /**
   * Verificar se token está expirado
   */
  public boolean isTokenExpired(String token) {
    token = token.replace("Bearer ", "");

    Algorithm algorithm = Algorithm.HMAC256(secretKey);

    try {
      var verifier = JWT.require(algorithm)
          .withIssuer("inovatech-eventos")
          .build();

      var decodedJWT = verifier.verify(token);
      return decodedJWT.getExpiresAt().before(java.util.Date.from(Instant.now()));
    } catch (JWTVerificationException ex) {
      return true; // Se não conseguir verificar, considera expirado
    }
  }

  /**
   * Extrair ID do usuário do token
   */
  public UUID getUserIdFromToken(String token) {
    String userId = validateToken(token);
    if (!userId.isEmpty()) {
      try {
        return UUID.fromString(userId);
      } catch (IllegalArgumentException ex) {
        System.err.println("UserID inválido no token: " + userId);
        return null;
      }
    }
    return null;
  }

  /**
   * Gerar data de expiração do token
   */
  private Instant genExpirationDate() {
    return LocalDateTime.now().plusSeconds(expiration / 1000).toInstant(ZoneOffset.of("-03:00"));
  }
}