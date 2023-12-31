package com.alvaro.empresas.passagens.autobuses.resources;

import com.alvaro.empresas.passagens.autobuses.dtos.PisoDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.PisoDTOResponse;
import com.alvaro.empresas.passagens.autobuses.dtos.PisoDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.services.AsientoBloqueadosService;
import com.alvaro.empresas.passagens.autobuses.services.PisoService;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pisos")
public class PisoResource {
    @Autowired
    private PisoService pisoService;
    @Autowired
    private AsientoBloqueadosService asientoBloqueadosService;

    @GetMapping("/{id}")
    public ResponseEntity<PisoDTOResponse> getOne(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok(pisoService.getOne(id));
    }

    @GetMapping
    public ResponseEntity<List<PisoDTOResponse>> findAll() {
        return ResponseEntity.ok().body(pisoService.findAll());
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid PisoDTO dto) {
        var saved = pisoService.save(dto);
        if (saved == null) {
            return ResponseEntity.unprocessableEntity().body(new Mensaje(
                    "La flota ya alcanzo el limite de numero de pisos o el autobus ya tiene trayectos guardados"
            ));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") Integer id) {
        var model = pisoService.findById(id);
        if (!model.getAutobus().getTrayectos().isEmpty()) {
            return ResponseEntity.ok().body(new Mensaje("Eliminado"));
        }
        pisoService.delete(model);
        return ResponseEntity.noContent().build();

    }
}
