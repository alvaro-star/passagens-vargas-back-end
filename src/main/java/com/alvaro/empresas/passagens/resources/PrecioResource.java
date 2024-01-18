package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.precios.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.precios.PrecioDTOResponseViaje;
import com.alvaro.empresas.passagens.dtos.precios.PrecioDTOUpdate;
import com.alvaro.empresas.passagens.services.PrecioService;
import com.alvaro.empresas.passagens.services.ViajeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/precios")
@SecurityRequirement(name = "bearer-key")
public class PrecioResource {
    @Autowired
    private PrecioService precioService;
    @Autowired
    private ViajeService viajeService;

    @GetMapping("/{id}")
    public ResponseEntity<PrecioDTO> getOne(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(precioService.getOne(id));
    }

    @GetMapping("/{id}/vender")
    public ResponseEntity<PrecioDTOResponseViaje> vender(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok().body(precioService.vender(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrecioDTO> update(@PathVariable(value = "id") UUID id, @Valid @RequestBody PrecioDTOUpdate dto) {
        return ResponseEntity.ok(precioService.update(dto, id));
    }
}
