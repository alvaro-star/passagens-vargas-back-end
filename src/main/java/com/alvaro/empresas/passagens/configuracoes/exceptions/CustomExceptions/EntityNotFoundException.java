package com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {
    private static final String message = "Entity not found";
    private final String id;
    private final Class<?> entityClass;

    public EntityNotFoundException(Object id, Class<?> entityClass) {
        super(message);
        this.id = id.toString();
        this.entityClass = entityClass;
    }
}
