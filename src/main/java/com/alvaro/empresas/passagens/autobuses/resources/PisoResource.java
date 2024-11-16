package com.alvaro.empresas.passagens.autobuses.resources;

import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.services.PisoService;
import com.alvaro.empresas.passagens.helpers.beans.MyUserComponent;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pisos")
@SecurityRequirement(name = "bearer-key")
public class PisoResource {
    private final PisoService pisoService;
    private final MyUserComponent myUserComponent;

    @Autowired
    public PisoResource(PisoService pisoService, MyUserComponent myUserComponent) {
        this.pisoService = pisoService;
        this.myUserComponent = myUserComponent;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PisoDTOResponse> getOne(@PathVariable Integer id) {
        return ResponseEntity.ok(pisoService.getOne(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody @Valid PisoDTOUpdate dto) {
        var piso = pisoService.findById(id);
        var user = myUserComponent.getUser();
        user.validIfIsMyEmpresa(piso.getAutobus().getEmpresaId());
        var updated = pisoService.update(dto, piso);
        return ResponseEntity.ok().body(updated);
    }
}
