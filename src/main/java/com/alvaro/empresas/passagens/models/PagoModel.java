package com.alvaro.empresas.passagens.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class PagoModel {
    private UUID id;
    private Float valor;
    private Float descuento;
    private Float tasa;
    private LocalDateTime fechaPago;
}
