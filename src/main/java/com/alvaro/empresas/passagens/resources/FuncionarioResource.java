package com.alvaro.empresas.passagens.resources;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.alvaro.empresas.passagens.dtos.FuncionarioResponseDTO;
import com.alvaro.empresas.passagens.dtos.PageOutput;
import com.alvaro.empresas.passagens.security.dtos.RegisterDTOFuncionario;
import com.alvaro.empresas.passagens.services.FuncionarioService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("funcionarios")
@SecurityRequirement(name = "bearer-key")
public class FuncionarioResource {
    @Autowired
    private FuncionarioService funcionarioService;

    @GetMapping("{idEmpresa}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public PageOutput<FuncionarioResponseDTO> findAll(@PathVariable UUID idEmpresa, Pageable pageable) {
        return funcionarioService.findAllFromEmpresa(idEmpresa, pageable);
    }

    @PostMapping("{idEmpresa}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void save(@RequestBody @Valid RegisterDTOFuncionario registerDTO, @PathVariable UUID idEmpresa) {
        funcionarioService.save(registerDTO, idEmpresa);
    }

    @DeleteMapping("{idEmpresa}/{email}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "idEmpresa") UUID idEmpresa, @PathVariable(value = "email") String email) {
        funcionarioService.delete(email, idEmpresa);
    }
}
