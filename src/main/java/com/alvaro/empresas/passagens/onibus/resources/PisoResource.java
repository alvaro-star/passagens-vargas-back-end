package com.alvaro.empresas.passagens.onibus.resources;

import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoDTOUpdate;
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
    @Autowired
    private UserLoguedComponent userLogued;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public PisoDTOResponse findById(@PathVariable UUID id) {
        return pisoService.findById(id);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public Object update(@PathVariable UUID id, @RequestBody @Valid PisoDTOUpdate dto) {
        var piso = pisoService.findById(id);
        userLogued.validIfIsMyEmpresa(piso.getOnibus().getEmpresaId());
        return pisoService.update(dto, piso);
    }
}