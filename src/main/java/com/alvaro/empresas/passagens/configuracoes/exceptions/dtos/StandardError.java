package com.alvaro.empresas.passagens.configuracoes.exceptions.dtos;

import java.io.Serializable;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
@NoArgsConstructor
public class StandardError implements Serializable {
    private Long timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;

    public StandardError(Long timestamp, HttpStatus status, String message, String path) {
        this.timestamp = timestamp;
        this.status = status.value();
        this.error = status.toString();
        this.message = message;
        this.path = path;
    }
}