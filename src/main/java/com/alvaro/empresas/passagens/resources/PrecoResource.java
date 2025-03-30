package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.precos.PrecoDTO;
import com.alvaro.empresas.passagens.dtos.precos.PrecoDTOResponseViagem;
import com.alvaro.empresas.passagens.dtos.precos.PrecoDTOUpdate;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.helpers.validators.ValidEnabledEntities;
import com.alvaro.empresas.passagens.services.PrecoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/precos")
@SecurityRequirement(name = "bearer-key")
public class PrecoResource {

    @Autowired
    private PrecoService precoService;
    @Autowired
    private UserLoguedComponent userLogued;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public PrecoDTO findById(@PathVariable(value = "id") UUID id) {
        return precoService.findById(id);
    }

    @GetMapping("{id}/vender")
    @ResponseStatus(HttpStatus.OK)
    public PrecoDTOResponseViagem vender(@PathVariable(value = "id") UUID id) {
        return precoService.vender(id);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public PrecoDTO update(@PathVariable(value = "id") UUID id, @RequestBody @Valid PrecoDTOUpdate dto) {
        var preco = precoService.findById(id);
        userLogued.validIfIsMyEmpresa(preco.getEmpresaId());
        ValidEnabledEntities.validEmpresa(preco.getEmpresa());

        return precoService.update(dto, preco);
    }
}