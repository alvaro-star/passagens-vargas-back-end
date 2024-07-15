package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.FuncionarioDTO;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.helpers.beans.MyUserService;
import com.alvaro.empresas.passagens.security.dtos.RegisterDtoFuncionario;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.services.EmpresaService;
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
    private final MyUserService myUserService;
    private final FuncionarioService funcionarioService;
    private final EmpresaService empresaService;

    @Autowired
    public FuncionarioResource(
            MyUserService myUserService,
            FuncionarioService funcionarioService,
            EmpresaService empresaService
    ) {
        this.myUserService = myUserService;
        this.funcionarioService = funcionarioService;
        this.empresaService = empresaService;
    }

    @GetMapping("/{idEmpresa}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Page<FuncionarioDTO>> getAll(@PathVariable(value = "idEmpresa") UUID idEmpresa, Pageable pageable) {
        var user = myUserService.getUser();
        if (user.hasRole(RoleList.ROLE_ADMIN.toString()) || user.isMyEmpresa(idEmpresa))
            return ResponseEntity.ok(funcionarioService.findAllFromEmpresa(idEmpresa, pageable));
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/{idEmpresa}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> save(@RequestBody @Valid RegisterDtoFuncionario registerDto, @PathVariable(value = "idEmpresa") UUID idEmpresa) {
        var user = myUserService.getUser();
        var empresa = empresaService.findById(idEmpresa);
        if (!user.isMyEmpresa(idEmpresa))
            return ResponseEntity.badRequest().body(new Mensaje("El administrador no esta relacionado con ninguna empresa"));
        if (empresa.getBloqued() || !empresa.getEnabled())
            return ResponseEntity.badRequest().body(new Mensaje("La empresa esta bloqueada"));
        Mensaje mensaje = funcionarioService.save(registerDto, idEmpresa);
        if (!mensaje.conteudo().equals(""))
            return ResponseEntity.badRequest().body(mensaje);
        return ResponseEntity.ok(new Mensaje("criado"));
    }

    @DeleteMapping("/{idEmpresa}/{email}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable(value = "idEmpresa") UUID idEmpresa, @PathVariable(value = "email") String email) {
        var user = myUserService.getUser();
        var empresa = empresaService.findById(idEmpresa);
        if (!user.isMyEmpresa(idEmpresa))
            return ResponseEntity.badRequest().body(new Mensaje("El administrador no esta relacionado con ninguna empresa"));
        if (empresa.getBloqued() || !empresa.getEnabled())
            return ResponseEntity.badRequest().body(new Mensaje("La empresa esta suspendida"));
        if (user.getLogin().equals(email))
            return ResponseEntity.badRequest().body(new Mensaje("Usted no puede autoeliminar-se"));
        Mensaje mensaje = funcionarioService.delete(email);
        if (!mensaje.conteudo().equals(""))
            return ResponseEntity.badRequest().body(mensaje);
        return ResponseEntity.noContent().build();
    }
}
