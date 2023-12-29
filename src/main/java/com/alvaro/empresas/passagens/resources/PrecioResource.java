package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.dtos.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.PrecioDTOUpdate;
import com.alvaro.empresas.passagens.services.PrecioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/precios")
public class PrecioResource {
    @Autowired
    private PrecioService precioService;

    @GetMapping("/{id}")
    public ResponseEntity<PrecioDTO> getOne(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(precioService.getOne(id));
    }

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody PrecioDTO dto) {
        var response = precioService.save(dto);
        if (response == null) {
            return ResponseEntity.unprocessableEntity().body(new Mensaje("No se pueden agragar mas precios"));
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrecioDTO> update(@PathVariable(value = "id") UUID id, @Valid @RequestBody PrecioDTOUpdate dto) {
        return ResponseEntity.ok(precioService.update(dto, id));
    }
}
