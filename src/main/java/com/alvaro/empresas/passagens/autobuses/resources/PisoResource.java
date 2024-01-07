package com.alvaro.empresas.passagens.autobuses.resources;

import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.services.PisoService;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pisos")
@SecurityRequirement(name = "bearer-key")
public class PisoResource {
    @Autowired
    private PisoService pisoService;

    @GetMapping("/{id}")
    public ResponseEntity<PisoDTOResponse> getOne(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok(pisoService.getOne(id));
    }

    @GetMapping
    public ResponseEntity<Page<PisoDTOResponse>> findAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok().body(pisoService.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable(value = "id") Integer id, @RequestBody @Valid PisoDTOUpdate dto) {
        var updated = pisoService.update(dto, id);
        if (updated == null) {
            return ResponseEntity.unprocessableEntity().body(new Mensaje(
                    "La flota ya tiene trayectos guardados"
            ));
        }
        return ResponseEntity.ok().body(updated);
    }
}
