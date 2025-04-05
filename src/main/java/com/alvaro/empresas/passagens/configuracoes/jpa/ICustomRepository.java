package com.alvaro.empresas.passagens.configuracoes.jpa;

public interface ICustomRepository<T, U> {
    T findByIdOrThr(U id);

    T findByIdOrThr(U id, String fieldName);
}
