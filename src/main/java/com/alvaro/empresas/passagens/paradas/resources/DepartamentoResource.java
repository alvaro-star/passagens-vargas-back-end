package com.alvaro.empresas.passagens.paradas.resources;

import com.alvaro.empresas.passagens.paradas.dtos.CiudadDTO;
import com.alvaro.empresas.passagens.paradas.dtos.DepartamentoDTO;
import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import com.alvaro.empresas.passagens.paradas.services.DepartamentoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/departamentos")
@SecurityRequirement(name = "bearer-key")
public class DepartamentoResource {
    @Autowired
    private DepartamentoService departamentoService;

    @GetMapping
    public ResponseEntity<Page<DepartamentoDTO>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok().body(departamentoService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoDTO> getOne(@PathVariable(value = "id") Integer id) {
        DepartamentoModel model = departamentoService.findById(id);

        List<CiudadDTO> ciudades = new ArrayList<>();
        model.getCiudades().forEach(ciudadModel -> {
            ciudades.add(new CiudadDTO(ciudadModel, model.getId()));
        });
        return ResponseEntity.ok().body(new DepartamentoDTO(model, ciudades));
    }

    @PostMapping
    public ResponseEntity<DepartamentoDTO> save(@RequestBody @Valid DepartamentoDTO dto) {
        DepartamentoModel model = departamentoService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new DepartamentoDTO(model));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoDTO> update(@PathVariable(value = "id") Integer id, @RequestBody @Valid DepartamentoDTO dto) {
        DepartamentoModel model = departamentoService.update(dto, id);
        return ResponseEntity.ok().body(new DepartamentoDTO(model));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") Integer id) {
        DepartamentoModel model = departamentoService.findById(id);
        departamentoService.eliminar(model);
        return ResponseEntity.noContent().build();
    }
}
