package com.alvaro.empresas.passagens.resources;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.alvaro.empresas.passagens.dtos.pasagens.PassagemDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.precos.PrecoDTO;
import com.alvaro.empresas.passagens.dtos.precos.PrecoDTOResponseViagem;
import com.alvaro.empresas.passagens.dtos.precos.PrecoDTOUpdate;
import com.alvaro.empresas.passagens.services.PassagemService;
import com.alvaro.empresas.passagens.services.PrecoService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("precos")
@SecurityRequirement(name = "bearer-key")
public class PrecoResource {

    @Autowired
    private PrecoService precoService;
    @Autowired
    private PassagemService passagemService;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public PrecoDTO findById(@PathVariable UUID id) {
        return precoService.findById(id);
    }

    @GetMapping("{id}/vender")
    @ResponseStatus(HttpStatus.OK)
    public PrecoDTOResponseViagem vender(@PathVariable UUID id) {
        return precoService.vender(id);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public PrecoDTO update(@PathVariable UUID id, @RequestBody @Valid PrecoDTOUpdate dto) {
        return precoService.update(id, dto);
    }

    @GetMapping("{id}/passagens")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_FUNCIONARIO', 'ROLE_EMPRESA_ADMIN', 'ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public List<PassagemDTOEmpresaResponse> getPassagensFromPreco(@PathVariable UUID idPreco) {

        return passagemService.getPassagensByPreco(idPreco);
    }
}