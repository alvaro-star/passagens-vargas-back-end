package com.alvaro.empresas.passagens.onibus.dtos.onibus;

import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoDTOCreate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record OnibusDTO(
        Integer id,
        @Pattern(regexp = "^\\d{4}[A-Z]{3}$", message = "Formato inválido, deve ser 1111AAA")
        String placa,
        @NotNull(message = "Não pode ser nulo")
        UUID idEmpresa,
        @Size(min = 1, max = 2)
        List<PisoDTOCreate> pisos
) {
    public OnibusDTO(String placa) {
        this(null, placa, null, new ArrayList<PisoDTOCreate>());
    }
}