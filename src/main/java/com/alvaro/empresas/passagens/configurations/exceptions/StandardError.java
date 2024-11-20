package com.alvaro.empresas.passagens.configurations.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class StandardError implements Serializable {

    private static final long serialVersionUIDLONG = 1L;
    private Long timestamp;
    private Integer status;
    private String error;
    private String message;
    private String conteudo;
    private String path;


    public StandardError(Long timestamp, Integer status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.conteudo = message;
        this.path = path;
    }
}