package com.alvaro.empresas.passagens.services.RepositoryMocks;

import com.alvaro.empresas.passagens.onibus.models.OnibusModel;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import org.springframework.stereotype.Service;

@Service
public class AutobusRepositoryMock {
    private Integer idAutobus = 1;

    public AutobusRepositoryMock() {
        this.idAutobus = 1;
    }

    public Integer generateId() {
        return ++idAutobus;
    }

    public OnibusModel generateAutobus(String placa, boolean enabled, EmpresaModel empresa) {
        var autobus = new OnibusModel(placa, enabled, empresa);
        autobus.setId(generateId());
        return autobus;
    }
}
