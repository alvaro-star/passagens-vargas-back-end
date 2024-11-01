package com.alvaro.empresas.passagens.configurations.exceptions.InternalException;

import com.alvaro.empresas.passagens.helpers.Mensaje;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private Mensaje mensaje;

    public BadRequestException(String mensaje) {
        super(mensaje);
        this.mensaje = new Mensaje(mensaje);
    }
}
