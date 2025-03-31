package com.alvaro.empresas.passagens.onibus.resources;

import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusCreateDTO;
import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusDTOResponse;
import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusUpdateDTO;
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

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public OnibusDTOResponse findById(@PathVariable UUID id) {
        return onibusService.findById(id);
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