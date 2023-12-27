package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.dtos.TrayectoDto;
import com.alvaro.empresas.passagens.services.TrayectoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trayectos")
public class TrayectoResource {
    @Autowired
    private TrayectoService trayectoService;

    @GetMapping
    public ResponseEntity<List<TrayectoDto>> getAll() {
        return ResponseEntity.ok().body(trayectoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrayectoDto> getOne(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok().body(trayectoService.getOne(id));
    }

    @PostMapping
    public ResponseEntity<TrayectoDto> save(@RequestBody @Valid TrayectoDto dto) {
        return ResponseEntity.ok().body(trayectoService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrayectoDto> update(@PathVariable(value = "id") UUID id, @RequestBody @Valid TrayectoDto dto) {
        return ResponseEntity.ok().body(trayectoService.update(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mensaje> delete(@PathVariable(value = "id") UUID id) {
        var model = trayectoService.findById(id);
        if (!model.getViajes().isEmpty()) {
            return ResponseEntity.badRequest().body(new Mensaje("El trayecto tiene viajes associados"));
        }
        if (!model.getPasajes().isEmpty()) {
            return ResponseEntity.badRequest().body(new Mensaje("El trayecto tiene pasajes associados"));
        }
        if (!model.getParadas().isEmpty()) {
            return ResponseEntity.badRequest().body(new Mensaje("El trayecto tiene paradas associados"));
        }
        trayectoService.delete(model);
        return ResponseEntity.ok().body(new Mensaje("El trayecto fue eliminado"));
    }
}
