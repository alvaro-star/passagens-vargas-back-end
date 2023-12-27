package com.alvaro.empresas.passagens.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ParadaDTO {
    private int id;
    @NotNull
    private LocalDateTime dataHora;
    @NotNull
    private int idLugar;
    @NotNull
    private int idTrayecto;
}
