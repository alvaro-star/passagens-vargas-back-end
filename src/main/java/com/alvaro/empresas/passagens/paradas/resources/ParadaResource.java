package com.alvaro.empresas.passagens.paradas.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOUpdate;
import com.alvaro.empresas.passagens.paradas.services.ParadaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paradas")
@SecurityRequirement(name = "bearer-key")
public class ParadaResource {
    @Autowired
    private ParadaService paradaService;

    @GetMapping
    public ResponseEntity<Page<ParadaDTO>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(paradaService.getAll(pageable));
    }

    @PostMapping
    public ResponseEntity<ParadaDTO> save(@RequestBody @Valid ParadaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paradaService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParadaDTOComplete> getOne(@PathVariable Integer id) {
        return ResponseEntity.ok(paradaService.getOne(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParadaDTO> update(@Valid @RequestBody ParadaDTOUpdate dto, @PathVariable Integer id) {
        return ResponseEntity.ok(paradaService.update(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mensaje> delete(@PathVariable Integer id) {
        var model = paradaService.findById(id);
        var viajes = model.getTrayecto().getViajes();
        if (!viajes.isEmpty())
            return ResponseEntity.badRequest().body(new Mensaje("La parada no puede ser eliminada pues el trayecto ya posse un viaje regsitrado"));

        paradaService.delete(model);
        return ResponseEntity.noContent().build();
    }
}
