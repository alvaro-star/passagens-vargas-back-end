package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.EmpresaDTO;
import com.alvaro.empresas.passagens.dtos.EmpresaDTOResponse;
import com.alvaro.empresas.passagens.helpers.Mensaje;
import com.alvaro.empresas.passagens.security.dtos.RegisterDtoEmpresaAdmin;
import com.alvaro.empresas.passagens.services.EmpresaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("empresas")
@SecurityRequirement(name = "bearer-key")
public class EmpresaResource {
    @Autowired
    private EmpresaService empresaService;

    @PostMapping("admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void registerEmpresaAdmin(@RequestBody @Valid RegisterDtoEmpresaAdmin empresaAdmin) {
        empresaService.saveAdmin(empresaAdmin);
    }

    @DeleteMapping("admin/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerEmpresario(@PathVariable(value = "email") String email) {
        empresaService.removerAdmin(email);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public Page<EmpresaDTOResponse> getAll(@PageableDefault Pageable pageable) {
        return empresaService.findAll(pageable);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmpresaDTOResponse getOne(@PathVariable UUID id) {
        return empresaService.getOne(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public EmpresaDTOResponse save(@RequestBody @Valid EmpresaDTO dto) {
        return empresaService.save(dto);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public EmpresaDTOResponse update(@PathVariable UUID id, @RequestBody @Valid EmpresaDTO dto) {
        return empresaService.update(dto, id);
    }

    @GetMapping("{id}/bloquedCount")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disabled(@PathVariable UUID id) {
        empresaService.bloquedCount(id);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        empresaService.delete(id);
    }
}