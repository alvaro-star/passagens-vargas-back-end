package com.alvaro.empresas.passagens.security.services;

import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(UsuarioModel usuarioModel) throws JWTCreationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("API passagens-back")
                .withSubject(usuarioModel.getEmail())
                .withExpiresAt(this.getExpirationDate())
                .sign(algorithm);
    }

    public String generateRefreshToken(UsuarioModel usuarioModel) throws JWTCreationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("API passagens-back")
                .withSubject(usuarioModel.getEmail())
                .withExpiresAt(this.getExpirationRefreshDate())
                .sign(algorithm);
    }


    public String validateToken(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm)
                .withIssuer("API passagens-back")
                .build()
                .verify(token)
                .getSubject();
    }

    private Instant getExpirationDate() {
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-04:00"));
    }

    private Instant getExpirationRefreshDate() {
        return LocalDateTime.now().plusHours(24).toInstant(ZoneOffset.of("-04:00"));
    }
}
