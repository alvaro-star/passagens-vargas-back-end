package com.alvaro.empresas.passagens.configuracoes.exceptions.dtos;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class StandardError implements Serializable {
    private Long timestamp;
    private Integer status;
    private String error;
    private String message;
    private String conteudo;
    private String path;

    public StandardError(Long timestamp, HttpStatus status, String message, String path) {
        this.timestamp = timestamp;
        this.status = status.value();
        this.error = status.toString();
        this.message = message;
        this.conteudo = message;
        this.path = path;
    }
}