package com.alvaro.empresas.passagens.autobuses.dtos.autobuses;

import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOCreate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record AutobusDTO(
        Integer id,
        @Pattern(regexp = "^\\d{4}[A-Z]{3}$", message = "Formato inválido, deve ser 1111AAA")
        String placa,
        @NotNull(message = "No puede ser nulo")
        UUID idEmpresa,
        @Size(min = 1, max = 2)
        List<PisoDTOCreate> pisos
) {
    public AutobusDTO(String placa) {
        this(null, placa, null, new ArrayList<PisoDTOCreate>());
    }
}
