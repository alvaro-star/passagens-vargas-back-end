package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.FuncionarioDTO;
import com.alvaro.empresas.passagens.helpers.Mensaje;
import com.alvaro.empresas.passagens.helpers.beans.MyUserComponent;
import com.alvaro.empresas.passagens.security.dtos.RegisterDtoFuncionario;
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
    @Autowired
    private MyUserComponent myUserComponent;
    @Autowired
    private FuncionarioService funcionarioService;
    @Autowired
    private EmpresaService empresaService;

    @GetMapping("/{idEmpresa}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Page<FuncionarioDTO>> getAll(@PathVariable UUID idEmpresa, Pageable pageable) {
        var user = myUserComponent.getUser();
        if (!user.isAdminOrOwnerEmpresa(idEmpresa))
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(funcionarioService.findAllFromEmpresa(idEmpresa, pageable));
    }

    @PostMapping("/{idEmpresa}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> save(@RequestBody @Valid RegisterDtoFuncionario registerDto, @PathVariable(value = "idEmpresa") UUID idEmpresa) {
        var user = myUserComponent.getUser();
        var empresa = empresaService.findById(idEmpresa);
        if (!user.isMyEmpresa(idEmpresa))
            return ResponseEntity.badRequest().body(new Mensaje("El administrador no esta relacionado con ninguna empresa"));
        if (empresa.getBloqued() || !empresa.getEnabled())
            return ResponseEntity.badRequest().body(new Mensaje("La empresa esta bloqueada"));
        Mensaje mensaje = funcionarioService.save(registerDto, idEmpresa);
        if (!mensaje.conteudo().isEmpty())
            return ResponseEntity.badRequest().body(mensaje);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{idEmpresa}/{email}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable(value = "idEmpresa") UUID idEmpresa, @PathVariable(value = "email") String email) {
        var user = myUserComponent.getUser();
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
