package com.alvaro.empresas.passagens.security.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.security.dtos.UsuarioDTO;
import com.alvaro.empresas.passagens.security.dtos.UsuarioDTOUpdate;
import com.alvaro.empresas.passagens.security.dtos.UsuarioDTOUpdateValidation;
import com.alvaro.empresas.passagens.security.dtos.UsuarioEmpresaDTO;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.services.UsuarioService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("usuarios")
@SecurityRequirement(name = "bearer-key")
public class UsuarioResource {

    @Autowired
    private UserLoguedComponent usuarioLogado;

    @Autowired
    private UsuarioService servicoUsuario;

    @GetMapping("mydata")
    @ResponseStatus(HttpStatus.OK)
    public Object obterPerfil() {
        var usuario = usuarioLogado.getUserModel();
        boolean ehEmpresa = usuario.hasRole(RoleList.ROLE_EMPRESA_FUNCIONARIO) || usuario.hasRole(RoleList.ROLE_EMPRESA_ADMIN);
        if (ehEmpresa)
            return new UsuarioEmpresaDTO(usuario);
        return new UsuarioDTO(usuario);
    }

    @PutMapping("update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarPerfil(@RequestBody @Valid UsuarioDTOUpdate solicitacao) {
        servicoUsuario.updateProfile(solicitacao);
    }

    @PutMapping("validar_update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void validarAtualizacao(@RequestBody @Valid UsuarioDTOUpdateValidation formulario) {
        servicoUsuario.validateUpdate(formulario);
    }
}
