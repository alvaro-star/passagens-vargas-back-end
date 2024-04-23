package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.dtos.TrayectoDTO;
import com.alvaro.empresas.passagens.dtos.TrayectoDTOResponse;
import com.alvaro.empresas.passagens.services.TrayectoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/trayectos")
@SecurityRequirement(name = "bearer-key")
public class TrayectoResource {
    @Autowired
    private TrayectoService trayectoService;

    @GetMapping
    public ResponseEntity<Page<TrayectoDTO>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(trayectoService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrayectoDTOResponse> getOne(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(trayectoService.getOne(id));
    }

    @PostMapping
    public ResponseEntity<TrayectoDTO> save(@RequestBody @Valid TrayectoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trayectoService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrayectoDTO> update(@PathVariable(value = "id") UUID id, @RequestBody @Valid TrayectoDTO dto) {
        return ResponseEntity.ok(trayectoService.update(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mensaje> delete(@PathVariable(value = "id") UUID id) {
        var model = trayectoService.findById(id);
        if (!model.getViajes().isEmpty()) {
            return ResponseEntity.badRequest().body(new Mensaje("El trayecto tiene viajes associados"));
        }
        if (!model.getPagos().isEmpty()) {
            return ResponseEntity.badRequest().body(new Mensaje("El trayecto tiene pasajes associados"));
        }
        if (!model.getParadas().isEmpty()) {
            return ResponseEntity.badRequest().body(new Mensaje("El trayecto tiene paradas associados"));
        }
        trayectoService.delete(model);
        return ResponseEntity.noContent().build();
    }
}