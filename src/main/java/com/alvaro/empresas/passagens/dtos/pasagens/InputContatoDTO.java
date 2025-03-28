package com.alvaro.empresas.passagens.dtos.pasagens;

import jakarta.validation.constraints.*;

public record InputContatoDTO(
        @NotBlank
        String nome,
        @Email
        String email,
        @NotBlank
        @Pattern(regexp = "^\\d{11}$", message = "O telefone precisa ter 11 digitos")
        String telefone
) {

}
