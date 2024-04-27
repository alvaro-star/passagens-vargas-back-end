package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOListBusqueda;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacao;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOResponse;
import com.alvaro.empresas.passagens.services.ViajeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/viajes")
@SecurityRequirement(name = "bearer-key")
public class ViajeResource {
    @Autowired
    private ViajeService viajeService;

    @GetMapping("/{id}")
    public ResponseEntity<ViajeDTOResponse> getOne(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(viajeService.getOne(id));
    }

    @PostMapping
    public ResponseEntity<List<ViajeDTOListBusqueda>> getViajeFromDia(@RequestBody @Valid ViajeDTOSolicitacao dto) {
        List<ViajeDTOListBusqueda> viajes = viajeService.getViajesFromDia(dto);
        return ResponseEntity.ok(viajes);
    }

}
