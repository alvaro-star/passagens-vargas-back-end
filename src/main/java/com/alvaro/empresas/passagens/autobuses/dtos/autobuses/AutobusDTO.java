package com.alvaro.empresas.passagens.autobuses.dtos.autobuses;

import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record AutobusDTO(
        Integer id,

        @NotBlank(message = "no puede ser nulo")
        String placa,

        @NotNull(message = "nao pode ser nulo")
        UUID idEmpresa,

        @Size(min = 1, max = 2)
        List<PisoDTO> pisos
) {
    public AutobusDTO(String placa) {
        this(null, placa, null, new ArrayList<PisoDTO>());
    }
}
