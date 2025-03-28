package com.alvaro.empresas.passagens.onibus.resources;

import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusDTO;
import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusDTOResponse;
import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusDTOUpdate;
import com.alvaro.empresas.passagens.onibus.services.OnibusService;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/onibus")
@SecurityRequirement(name = "bearer-key")
public class OnibusResource {
    @Autowired
    private OnibusService onibusService;
    @Autowired
    private UserLoguedComponent userLogued;

    @GetMapping("/from/{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public Page<OnibusDTOResponse> getOnibusFromEmpresa(
            @PathVariable UUID idEmpresa,
            @PageableDefault(sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable) {
        userLogued.validIfIsAdminOrOwnerEmpresa(idEmpresa);
        return onibusService.findAllFromEmpresa(idEmpresa, pageable);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public OnibusDTOResponse getOne(@PathVariable UUID id) {
        return onibusService.getOne(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public Object save(@RequestBody @Valid OnibusDTO dto, BindingResult bindingResult) {
        userLogued.validIfIsMyEmpresa(dto.idEmpresa());
        return onibusService.salvar(dto, bindingResult);
    }

    //Somente o administrador
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public Object update(@PathVariable UUID id, @RequestBody @Valid OnibusDTOUpdate dto, BindingResult bindingResult) {
        var onibus = onibusService.findById(id);
        userLogued.validIfIsMyEmpresa(onibus.getEmpresaId());
        return onibusService.update(dto, onibus, bindingResult);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public void delete(@PathVariable UUID id) {
        var model = onibusService.findById(id);
        userLogued.validIfIsMyEmpresa(model.getEmpresaId());
        onibusService.delete(model);
    }
}