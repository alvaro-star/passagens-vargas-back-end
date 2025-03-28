package com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {
    private static final String postFixEntity = "Model";
    private final String id;
    private final Class<?> entityClass;

    public static String clearEntityName(Class<?> entityClass) {
        String entityName = entityClass.getSimpleName();
        int sizeEntityName = entityName.length() - postFixEntity.length();
        if (sizeEntityName > 0 && entityName.endsWith(postFixEntity)) {
            entityName = entityName.substring(0, sizeEntityName - 1);
        }
        return entityName;
    }

    private static String mountMessage(String id, Class<?> entityClass) {
        String entityName = clearEntityName(entityClass);
        return "Entity " + entityName + " with id " + id + " not found";
    }

    public EntityNotFoundException(Object id, Class<?> entityClass) {
        super(mountMessage(id.toString(), entityClass));
        this.id = id.toString();
        this.entityClass = entityClass;
    }
}
