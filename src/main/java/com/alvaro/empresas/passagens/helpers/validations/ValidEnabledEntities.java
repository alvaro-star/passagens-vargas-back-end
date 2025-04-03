package com.alvaro.empresas.passagens.helpers.validations;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.onibus.models.OnibusModel;
import org.springframework.http.HttpStatus;

public class ValidEnabledEntities {
    public static void validOnibus(OnibusModel model) {
        if (!model.isEnabled())
            throw new RestRuntimeException(HttpStatus.CONFLICT, "El onibus no esta habilitado");
    }

    public static void validEmpresa(EmpresaModel model) {
        if (model.getBloqueado() || !model.getHabilitado())
            throw new RestRuntimeException(HttpStatus.CONFLICT, "La empresa esta inhabilitada");
    }
}
