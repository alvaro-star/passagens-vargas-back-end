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
@RequestMapping("/empresas")
@SecurityRequirement(name = "bearer-key")
public class EmpresaResource {

    @Autowired
    private EmpresaService empresaService;

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Mensaje> registerEmpresaAdmin(@RequestBody @Valid RegisterDtoEmpresaAdmin empresaAdmin) {
        empresaService.saveAdmin(empresaAdmin);
        return ResponseEntity.ok(new Mensaje("El cargo le fue dado"));
    }

    @DeleteMapping("/admin/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Mensaje> removerEmpresario(@PathVariable(value = "email") String email) {
        empresaService.removerAdmin(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<EmpresaDTOResponse>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(empresaService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDTOResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(empresaService.getOne(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<EmpresaDTOResponse> save(@RequestBody @Valid EmpresaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empresaService.save(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<EmpresaDTOResponse> update(@PathVariable UUID id, @RequestBody @Valid EmpresaDTO dto) {
        return ResponseEntity.ok(empresaService.update(dto, id));
    }

    @GetMapping("/{id}/bloquedCount")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> disabled(@PathVariable UUID id) {
        empresaService.bloquedCount(id);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable UUID id) {
        empresaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
