package com.alvaro.empresas.passagens.helpers.dtos;

public record DataHoraFormatada(
        String data,
        String hora
) {
    public String toString() {
        return hora + " - " + data;
    }
}
