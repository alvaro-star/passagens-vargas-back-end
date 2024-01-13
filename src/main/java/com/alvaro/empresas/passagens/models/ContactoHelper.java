package com.alvaro.empresas.passagens.models;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class ContactoHelper {
    @Email
    private String email;
    @NotNull
    private Integer telefono;
}
