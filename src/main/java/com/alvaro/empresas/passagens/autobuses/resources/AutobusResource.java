package com.alvaro.empresas.passagens.autobuses.resources;

import com.alvaro.empresas.passagens.autobuses.dtos.AutobusDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.AutobusDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.services.AutobusService;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/autobuses")
public class AutobusResource {
    @Autowired
    private AutobusService autobusService;

    @GetMapping
    public ResponseEntity<List<AutobusDTO>> getAll() {
        return ResponseEntity.ok().body(autobusService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutobusDTO> getOne(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok().body(autobusService.getOne(id));
    }

    @PostMapping
    public ResponseEntity<AutobusDTO> save(@RequestBody @Valid AutobusDTO dto) {
        return ResponseEntity.ok().body(autobusService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutobusDTO> update(@PathVariable(value = "id") Integer id, @RequestBody @Valid AutobusDTOUpdate dto) {
        return ResponseEntity.ok().body(autobusService.update(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mensaje> delete(@PathVariable(value = "id") Integer id) {
        var model = autobusService.findById(id);
        if (!model.getTrayectos().isEmpty()) {
            return ResponseEntity.badRequest().body(new Mensaje("El autobus tiene trayectos registrados"));
        }
        if (!model.getPisos().isEmpty()) {
            return ResponseEntity.badRequest().body(new Mensaje("El autobus tiene pisos registrados"));
        }
        autobusService.delete(model);
        return ResponseEntity.ok().body(new Mensaje("El autobus fue eliminado"));
    }
}
