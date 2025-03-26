package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.dtos.FuncionarioDTO;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.security.dtos.RegisterDtoFuncionario;
import com.alvaro.empresas.passagens.services.FuncionarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/funcionarios")
@SecurityRequirement(name = "bearer-key")
public class FuncionarioResource {
    @Autowired
    private UserLoguedComponent userLogued;
    @Autowired
    private FuncionarioService funcionarioService;

    @GetMapping("/{idEmpresa}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public Page<FuncionarioDTO> getAll(@PathVariable UUID idEmpresa, Pageable pageable) {
        userLogued.validIfIsAdminOrOwnerEmpresa(idEmpresa);
        return funcionarioService.findAllFromEmpresa(idEmpresa, pageable);
    }

    @PostMapping("/{idEmpresa}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void save(@RequestBody @Valid RegisterDtoFuncionario registerDto, @PathVariable UUID idEmpresa) {
        userLogued.validIfIsMyEmpresa(idEmpresa);
        funcionarioService.save(registerDto, idEmpresa);
    }

    @DeleteMapping("/{idEmpresa}/{email}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "idEmpresa") UUID idEmpresa, @PathVariable(value = "email") String email) {
        userLogued.validIfIsMyEmpresa(idEmpresa);
        funcionarioService.delete(email, idEmpresa);
    }
}
