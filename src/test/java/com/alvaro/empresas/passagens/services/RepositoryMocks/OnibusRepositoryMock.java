package com.alvaro.empresas.passagens.services.RepositoryMocks;

import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusDTO;
import com.alvaro.empresas.passagens.onibus.models.OnibusModel;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import org.springframework.stereotype.Service;

@Service
public class OnibusRepositoryMock {
    private Integer idOnibus = 1;

    public OnibusRepositoryMock() {
        this.idOnibus = 1;
    }

    public Integer generateId() {
        return ++idOnibus;
    }

    public OnibusModel generateOnibus(String placa, boolean enabled, EmpresaModel empresa) {
        var onibusDTO = new OnibusDTO(placa);
        var onibus = new OnibusModel(onibusDTO, empresa);
        onibus.setHabilitado(enabled);
        return onibus;
    }
}
