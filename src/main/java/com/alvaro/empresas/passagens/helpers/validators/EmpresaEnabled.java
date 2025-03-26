package com.alvaro.empresas.passagens.helpers.validators;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.repositories.EmpresaRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EmpresaEnabled {
    @Autowired
    private EmpresaRepository empresaRepository;

    public void validEmpresaEnabled(UUID idEmpresa) {
        var empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new ObjectNotFoundException(idEmpresa, EmpresaModel.class.getName()));
        if (empresa.getBloqued() || !empresa.getEnabled())
            throw new RestRuntimeException(HttpStatus.CONFLICT, "La empresa esta inhabilitada");
    }
}
