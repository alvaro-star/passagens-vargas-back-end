package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.EmpresaDto;
import com.alvaro.empresas.passagens.dtos.EmpresaResponseDto;
import com.alvaro.empresas.passagens.dtos.Mensaje;
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
//Role_ADMIN, ROLE_EMPRESA_ADMIN
public class EmpresaResource {

    @Autowired
    private EmpresaService empresaService;

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Mensaje> registerEmpresaAdmin(@RequestBody @Valid RegisterDtoEmpresaAdmin empresaAdmin) {
        Mensaje mensaje;
        mensaje = empresaService.saveAdmin(empresaAdmin);
        if (mensaje.conteudo().isEmpty())
            return ResponseEntity.ok(new Mensaje("Criado con exito"));
        return ResponseEntity.badRequest().body(mensaje);
    }

    @DeleteMapping("/admin/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Mensaje> removerEmpresario(@PathVariable(value = "email") String email) {
        Mensaje mensaje;
        mensaje = empresaService.removerAdmin(email);
        if (mensaje.conteudo().isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.badRequest().body(mensaje);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<EmpresaResponseDto>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(empresaService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDto> getOne(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(empresaService.getOne(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<EmpresaResponseDto> save(@RequestBody @Valid EmpresaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empresaService.save(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<EmpresaResponseDto> update(@PathVariable(value = "id") UUID id, @RequestBody @Valid EmpresaDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(empresaService.update(dto, id));
    }

    @GetMapping("/{id}/bloquedCount")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> disabled(@PathVariable(value = "id") UUID id) {
        var empresa = empresaService.findById(id);
        if (!empresa.getEnabled())
            return ResponseEntity.badRequest().body(new Mensaje("La empresa esta deshabilitada"));
        empresaService.bloquedCount(empresa);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") UUID id) {
        empresaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
