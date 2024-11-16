package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.FuncionarioDTO;
import com.alvaro.empresas.passagens.helpers.beans.MyUserComponent;
import com.alvaro.empresas.passagens.security.dtos.RegisterDtoFuncionario;
import com.alvaro.empresas.passagens.services.FuncionarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/funcionarios")
@SecurityRequirement(name = "bearer-key")
//EMPRESA_ADMIN - EMPRESA_ADMIN
public class FuncionarioResource {
    @Autowired
    private MyUserComponent myUserComponent;
    @Autowired
    private FuncionarioService funcionarioService;

    @GetMapping("/{idEmpresa}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Page<FuncionarioDTO>> getAll(@PathVariable UUID idEmpresa, Pageable pageable) {
        var user = myUserComponent.getUser();
        user.validIfIsAdminOrOwnerEmpresa(idEmpresa);
        return ResponseEntity.ok(funcionarioService.findAllFromEmpresa(idEmpresa, pageable));
    }

    @PostMapping("/{idEmpresa}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> save(@RequestBody @Valid RegisterDtoFuncionario registerDto, @PathVariable UUID idEmpresa) {
        var user = myUserComponent.getUser();
        user.validIfIsMyEmpresa(idEmpresa);
        funcionarioService.save(registerDto, idEmpresa);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{idEmpresa}/{email}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable(value = "idEmpresa") UUID idEmpresa, @PathVariable(value = "email") String email) {
        var user = myUserComponent.getUser();
        user.validIfIsMyEmpresa(idEmpresa);
        funcionarioService.delete(email, idEmpresa);
        return ResponseEntity.noContent().build();
    }
}
