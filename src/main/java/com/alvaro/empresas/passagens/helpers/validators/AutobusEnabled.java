package com.alvaro.empresas.passagens.helpers.validators;

import com.alvaro.empresas.passagens.onibus.models.AutobusModel;
import com.alvaro.empresas.passagens.onibus.repositories.AutobusRepository;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AutobusEnabled {
    @Autowired
    private AutobusRepository autobusRepository;

    public void validAutobusEnabled(Integer id) {
        var autobus = autobusRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, AutobusModel.class.getName()));
        if (!autobus.isEnable())
            throw new RestRuntimeException(HttpStatus.CONFLICT, "El autobus no esta habilitado");
    }
}
