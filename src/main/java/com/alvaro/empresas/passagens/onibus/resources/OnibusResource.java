package com.alvaro.empresas.passagens.onibus.resources;

import java.util.UUID;

import com.alvaro.empresas.passagens.dtos.viagens.seller.ViagemResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.alvaro.empresas.passagens.dtos.PageOutput;
import com.alvaro.empresas.passagens.onibus.dtos.OnibusCreateDTO;
import com.alvaro.empresas.passagens.onibus.dtos.OnibusDTOResponse;
import com.alvaro.empresas.passagens.onibus.dtos.OnibusUpdateDTO;
import com.alvaro.empresas.passagens.onibus.services.OnibusService;
import com.alvaro.empresas.passagens.services.ViagemEmpresaService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("onibus")
@SecurityRequirement(name = "bearer-key")
public class OnibusResource {
    @Autowired
    private OnibusService onibusService;
    @Autowired
    private ViagemEmpresaService viagemEmpresaService;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public OnibusDTOResponse findById(@PathVariable UUID id) {
        return onibusService.findById(id);
    }

    @GetMapping("{id}/viagens")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public PageOutput<ViagemResponseDTO> findAllFromOnibus(
            @PathVariable UUID id,
            @RequestParam String mesAnalise,
            @PageableDefault(sort = "dataHoraSaida") Pageable pageable) {
        return viagemEmpresaService.findAllFromOnibus(id, mesAnalise, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public Object save(@RequestBody @Valid OnibusCreateDTO dto) {
        return onibusService.save(dto);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public Object update(@PathVariable UUID id, @RequestBody @Valid OnibusUpdateDTO dto) {
        return onibusService.update(id, dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public void delete(@PathVariable UUID id) {
        onibusService.delete(id);
    }
}