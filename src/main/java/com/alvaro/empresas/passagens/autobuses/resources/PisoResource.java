package com.alvaro.empresas.passagens.autobuses.resources;

import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.services.PisoService;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.helpers.beans.MyUserService;
import com.alvaro.empresas.passagens.services.EmpresaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/pisos")
@SecurityRequirement(name = "bearer-key")
public class PisoResource {
    private final PisoService pisoService;
    private final EmpresaService empresaService;
    private final MyUserService myUserService;

    @Autowired
    public PisoResource(PisoService pisoService, EmpresaService empresaService, MyUserService myUserService) {
        this.pisoService = pisoService;
        this.empresaService = empresaService;
        this.myUserService = myUserService;
    }

    private Mensaje validarUsuario(UUID idEmpresa) {
        var user = myUserService.getUser();
        if (user.getIdEmpresa() == null)
            return new Mensaje("Usted no esta relacionado a una empresa");
        var empresa = empresaService.findById(user.getIdEmpresa());
        if (empresa.getBloqued())
            return new Mensaje("La empresa esta bloqueada");
        if (!user.isMyEmpresa(idEmpresa))
            return new Mensaje("Usted no esta relacionado a esta empresa");
        return new Mensaje("");
    }

    @GetMapping("/{id}")
    public ResponseEntity<PisoDTOResponse> getOne(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok(pisoService.getOne(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> update(@PathVariable(value = "id") Integer id, @RequestBody @Valid PisoDTOUpdate dto) {
        var piso = pisoService.findById(id);
        var mensaje = validarUsuario(piso.getAutobus().getEmpresa().getId());

        if (!piso.getAutobus().isEnable())
            return ResponseEntity.badRequest().body(new Mensaje("El autobus esta deshabilitado"));

        if (mensaje.conteudo().isEmpty()) {
            var updated = pisoService.update(dto, piso);
            if (updated == null)
                return ResponseEntity.unprocessableEntity().body(new Mensaje("La flota ya tiene viajes almacenados"));
            return ResponseEntity.ok().body(updated);
        } else
            return ResponseEntity.badRequest().body(mensaje);


    }
}
