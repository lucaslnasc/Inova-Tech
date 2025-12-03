package com.inovatech.inovatech_eventos.security;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.inovatech.inovatech_eventos.model.User;
import com.inovatech.inovatech_eventos.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

  @Autowired
  private JWTProvider jwtProvider;

  @Autowired
  private UserService userService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    // Endpoints que não precisam de autenticação
    String requestURI = request.getRequestURI();
    if (isPublicEndpoint(requestURI)) {
      filterChain.doFilter(request, response);
      return;
    }

    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {
      try {
        // Validar o token
        UUID userId = jwtProvider.getUserIdFromToken(header);

        if (userId == null) {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.getWriter().write("{\"error\":\"Token inválido\"}");
          return;
        }

        // Buscar usuário no banco
        var userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.getWriter().write("{\"error\":\"Usuário não encontrado\"}");
          return;
        }

        User user = userOpt.get();

        // Adicionar informações do usuário na requisição
        request.setAttribute("userId", user.getId());
        request.setAttribute("userEmail", user.getEmail());
        request.setAttribute("userName", user.getName());
        request.setAttribute("userType", user.getType());

        // Criar authorities baseado no tipo de usuário
        var authority = new SimpleGrantedAuthority("ROLE_" + user.getType().name());

        // Configurar contexto de segurança - usar email como principal
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            user.getEmail(), // Email como principal para authentication.getName()
            null,
            Collections.singletonList(authority));

        // Configurar detalhes adicionais no Authentication
        auth.setDetails(user);

        SecurityContextHolder.getContext().setAuthentication(auth);

      } catch (Exception ex) {
        System.err.println("Erro na validação do token: " + ex.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\":\"Erro na autenticação\"}");
        return;
      }
    } else {
      // Token não fornecido para endpoint protegido
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("{\"error\":\"Token de acesso obrigatório\"}");
      return;
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Verificar se o endpoint é público (não precisa de autenticação)
   */
  private boolean isPublicEndpoint(String requestURI) {
    return requestURI.equals("/api/users/login") ||
        requestURI.equals("/api/users") && "POST".equals(getCurrentHttpMethod()) ||
        requestURI.startsWith("/api/users/check-email") ||
        requestURI.startsWith("/swagger-ui") ||
        requestURI.startsWith("/v3/api-docs") ||
        requestURI.equals("/");
  }

  /**
   * Obter método HTTP atual (helper method)
   */
  private String getCurrentHttpMethod() {
    var request = (HttpServletRequest) ((org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder
        .currentRequestAttributes()).getRequest();
    return request.getMethod();
  }
}