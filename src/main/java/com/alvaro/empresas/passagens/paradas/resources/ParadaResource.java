package com.alvaro.empresas.passagens.paradas.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOUpdate;
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
        return ResponseEntity.ok(paradaService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParadaDTO> getOne(@PathVariable Integer id) {
        return ResponseEntity.ok(paradaService.getOne(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParadaDTO> update(@Valid @RequestBody ParadaDTOUpdate dto, @PathVariable Integer id) {
        return ResponseEntity.ok(paradaService.update(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mensaje> delete(@PathVariable Integer id) {
        var model = paradaService.findById(id);
        if (!model.getSalidas().isEmpty() || !model.getDestinos().isEmpty()) {
            return ResponseEntity.badRequest().body(new Mensaje("La entidad esta associoado a un pasaje"));
        }

        paradaService.delete(model);
        return ResponseEntity.ok(new Mensaje("La parada fue eliminado con exito"));
    }
}
