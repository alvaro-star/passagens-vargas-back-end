package com.alvaro.empresas.passagens.autobuses.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public record AutobusDTOUpdate(
        @NotBlank
        String placa
) {
}
