package com.alvaro.empresas.passagens.paradas.resources;

import com.alvaro.empresas.passagens.paradas.dtos.CiudadDTO;
import com.alvaro.empresas.passagens.paradas.dtos.CiudadDtoUpdate;
import com.alvaro.empresas.passagens.paradas.services.CiudadService;
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

@RestController
@RequestMapping("/ciudades")
@SecurityRequirement(name = "bearer-key")
public class CiudadResource {
    @Autowired
    private CiudadService ciudadService;
    @Autowired
    private DepartamentoService departamentoService;

    @GetMapping
    public ResponseEntity<Page<CiudadDTO>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok().body(ciudadService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CiudadDTO> getOne(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok().body(ciudadService.getOne(id));
    }

    @GetMapping("/{nombre}/like")
    public ResponseEntity<Page<CiudadDTO>> getAllLike(@PathVariable(value = "nombre") String nombre, @PageableDefault(size = 8, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok().body(ciudadService.findByNombreContaining(nombre, pageable));
    }

    @PostMapping
    public ResponseEntity<CiudadDTO> save(@Valid @RequestBody CiudadDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ciudadService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CiudadDTO> update(@Valid @RequestBody CiudadDtoUpdate dto, @PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok().body(ciudadService.update(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") Integer id) {
        var model = ciudadService.findById(id);
        ciudadService.delete(model);
        return ResponseEntity.noContent().build();
    }
}
