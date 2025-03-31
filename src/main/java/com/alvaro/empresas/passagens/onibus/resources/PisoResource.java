package com.alvaro.empresas.passagens.onibus.resources;

import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoResponseDTO;
import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoUpdateDTO;
import com.alvaro.empresas.passagens.onibus.services.PisoService;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/pisos")
@SecurityRequirement(name = "bearer-key")
public class PisoResource {
    @Autowired
    private PisoService pisoService;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public PisoResponseDTO findById(@PathVariable UUID id) {
        return pisoService.findById(id);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public Object update(@PathVariable UUID id, @RequestBody @Valid PisoUpdateDTO dto) {
        return pisoService.update(id, dto);
    }
}