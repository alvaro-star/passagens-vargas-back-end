package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.*;
import com.alvaro.empresas.passagens.services.ViajeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/viajes")
public class ViajeResource {
    @Autowired
    private ViajeService viajeService;

    @GetMapping
    public ResponseEntity<List<ViajeDTOList>> getAll() {
        return ResponseEntity.ok(viajeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViajeDTOResponse> getOne(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok(viajeService.getOne(id));
    }

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody ViajeDTO dto) {
        ViajeDTOResponse response = viajeService.save(dto);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje("Las paradas no son validas"));
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable(value = "id") Integer id, @Valid @RequestBody ViajeDTOUpdate dto) {
        ViajeDTOResponse response = viajeService.update(dto, id);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje("Las paradas no son validas"));
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") Integer id) {
        var model = viajeService.findById(id);
        if (!model.getSillas().isEmpty()) {
            return ResponseEntity.badRequest().body(new Mensaje("El viaje posee pasajes registrados"));
        }
        viajeService.delete(model);
        return ResponseEntity.noContent().build();
    }
}
