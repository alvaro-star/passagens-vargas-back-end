package com.alvaro.empresas.passagens.autobuses.dtos.autobuses;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public record AutobusDTOUpdate(
        @NotBlank
        String placa,
        Boolean enable
) {
}
