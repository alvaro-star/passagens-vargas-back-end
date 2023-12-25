package com.alvaro.empresas.passagens.lugares.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.lugares.dtos.DepartamentoDTO;
import com.alvaro.empresas.passagens.lugares.models.DepartamentoModel;
import com.alvaro.empresas.passagens.lugares.services.DepartamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/departamentos")
public class DepartamentoResource {
    @Autowired
    private DepartamentoService departamentoService;

    @GetMapping
    public ResponseEntity<List<DepartamentoDTO>> getAll() {
        List<DepartamentoModel> models = departamentoService.findAll();
        List<DepartamentoDTO> dtos = new ArrayList<>();
        models.forEach(model -> {
            dtos.add(new DepartamentoDTO(model));
        });
        return ResponseEntity.ok().body(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoDTO> getOne(@PathVariable(value = "id") Integer id) {
        DepartamentoModel model = departamentoService.findById(id);
        return ResponseEntity.ok().body(new DepartamentoDTO(model));
    }

    @PostMapping
    public ResponseEntity<DepartamentoDTO> save(@RequestBody @Valid DepartamentoDTO dto) {
        DepartamentoModel model = departamentoService.save(dto);
        return ResponseEntity.ok().body(new DepartamentoDTO(model));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoDTO> update(@PathVariable(value = "id") Integer id, @RequestBody @Valid DepartamentoDTO dto) {
        DepartamentoModel model = departamentoService.update(dto, id);
        return ResponseEntity.ok().body(new DepartamentoDTO(model));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") Integer id) {
        DepartamentoModel model = departamentoService.findById(id);
        if (!model.getCiudades().isEmpty()) {
            return ResponseEntity.ok().body(new Mensaje("El departamento tiene ciudades registradas"));
        }
        departamentoService.eliminar(model);
        return ResponseEntity.ok().body(new Mensaje("El departamento fue eliminado"));
    }
}
