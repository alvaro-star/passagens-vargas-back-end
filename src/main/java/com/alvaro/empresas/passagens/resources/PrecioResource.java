package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.dtos.precios.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.precios.PrecioDTOResponseViaje;
import com.alvaro.empresas.passagens.dtos.precios.PrecioDTOUpdate;
import com.alvaro.empresas.passagens.helpers.beans.MyUserService;
import com.alvaro.empresas.passagens.services.PrecioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/precios")
@SecurityRequirement(name = "bearer-key")
public class PrecioResource {

    private final PrecioService precioService;
    private final MyUserService myUserService;

    @Autowired
    public PrecioResource(PrecioService precioService, MyUserService myUserService) {
        this.precioService = precioService;
        this.myUserService = myUserService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrecioDTO> getOne(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(precioService.getOne(id));
    }

    @GetMapping("/{id}/vender")
    public ResponseEntity<PrecioDTOResponseViaje> vender(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok().body(precioService.vender(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Object> update(@PathVariable(value = "id") UUID id, @RequestBody @Valid PrecioDTOUpdate dto) {
        var precio = precioService.findById(id);
        var usuario = myUserService.getUser();
        if (!usuario.isMyEmpresa(precio.getEmpresa().getId()))
            return ResponseEntity.badRequest().body(new Mensaje("Usted no esta relacionado con esta empresa"));
        if (precio.getEmpresa().getBloqued() || !precio.getEmpresa().getEnabled())
            return ResponseEntity.badRequest().body(new Mensaje("La empresa esta inhabilitada"));
        return ResponseEntity.ok(precioService.update(dto, precio));
    }
}
