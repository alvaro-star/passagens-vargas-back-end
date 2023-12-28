package com.alvaro.empresas.passagens.paradas.resources;

import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.services.ParadaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/paradas")
public class ParadaResource {
    @Autowired
    private ParadaService paradaService;

    @GetMapping
    public ResponseEntity<List<ParadaDTO>> getAll() {
        return ResponseEntity.ok(paradaService.getAll());
    }

    @PostMapping
    public ResponseEntity<ParadaDTO> save(@RequestBody @Valid ParadaDTO dto) {
        return ResponseEntity.ok(dto);
    }
}
