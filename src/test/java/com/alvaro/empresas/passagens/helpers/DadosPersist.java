package com.alvaro.empresas.passagens.helpers;

import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import jakarta.persistence.EntityManager;

import java.util.List;

public class DadosPersist {
    private final EntityManager manager;

    public DadosPersist(EntityManager manager) {
        this.manager = manager;
    }
}
