package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.precos.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.precos.PrecioDTOResponseViaje;
import com.alvaro.empresas.passagens.dtos.precos.PrecioDTOUpdate;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.helpers.validators.EmpresaEnabled;
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

    @Autowired
    private PrecioService precioService;
    @Autowired
    private UserLoguedComponent userLogued;
    @Autowired
    private EmpresaEnabled empresaEnabled;

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
        userLogued.validIfIsMyEmpresa(precio.getEmpresaId());
        empresaEnabled.validEmpresaEnabled(precio.getEmpresaId());

        return ResponseEntity.ok(precioService.update(dto, precio));
    }
}
