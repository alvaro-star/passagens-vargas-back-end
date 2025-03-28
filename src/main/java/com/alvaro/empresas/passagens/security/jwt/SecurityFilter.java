package com.alvaro.empresas.passagens.security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import com.alvaro.empresas.passagens.security.services.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SecurityFilter.class);
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            var token = this.getToken(request);
            if (!isNullOrBlank(token)) {
                var subject = tokenService.validateToken(token);
                if (!isNullOrBlank(subject)) {
                    UserDetails user = usuarioRepository.findByEmail(subject)
                            .orElseThrow(() -> new RuntimeException("Token Invalido"));
                    var authenticate = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticate);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        var authheader = request.getHeader("Authorization");
        if (authheader != null && authheader.startsWith("Bearer "))
            return authheader.replace("Bearer ", "");
        return null;
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }
}
