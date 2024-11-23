package com.alvaro.empresas.passagens.security.configurations.exceptions.Exeptions;

import com.alvaro.empresas.passagens.security.jwt.SecurityFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAutenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SecurityFilter.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        logger.error("Token Invalido");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
    }
}
