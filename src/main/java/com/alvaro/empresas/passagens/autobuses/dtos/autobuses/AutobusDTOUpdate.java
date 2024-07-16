package com.alvaro.empresas.passagens.autobuses.dtos.autobuses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

public record AutobusDTOUpdate(
        @NotBlank
        @Pattern(regexp = "^\\d{4}[A-Z]{3}$", message = "Formato inválido. Deve ser 1111AAA")
        String placa
) {
}
