package com.alvaro.empresas.passagens.security.jwt;

import com.alvaro.empresas.passagens.security.repositories.UserRepository;
import com.alvaro.empresas.passagens.security.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SecurityFilter.class);
    @Autowired
    TokenService tokenService;
    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            var token = this.getToken(request);
            var subject = tokenService.validateToken(token);
            UserDetails user = userRepository.findByLogin(subject);
            if (user != null) {
                var authenticate = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticate);
            } else {
                System.out.println("Sem Usuario logado");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        var authheader = request.getHeader("Authorization");
        if (authheader != null && authheader.startsWith("Bearer"))
            return authheader.replace("Bearer ", "");
        return null;
    }
}
