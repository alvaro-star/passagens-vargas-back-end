package com.alvaro.empresas.passagens.configurations.exceptions.InternalException;

import com.alvaro.empresas.passagens.helpers.Mensaje;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GeneralException extends RuntimeException {
    private final HttpStatus status;
    private final Mensaje mensaje;

    public GeneralException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        mensaje = new Mensaje(message);
    }

    public GeneralException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        mensaje = new Mensaje(message);
    }
}
