package com.alvaro.empresas.passagens.configuracoes.exceptions.dtos;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EntityNotFoundError extends StandardError {
    private final String id;
    private final String entity;

    public EntityNotFoundError(Long timestamp, HttpStatus status, String path, String message, String id, String entityName) {
        super(timestamp, status, message, path);
        this.id = id;
        this.entity = entityName;
    }
}
