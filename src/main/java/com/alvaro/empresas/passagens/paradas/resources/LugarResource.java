package com.alvaro.empresas.passagens.paradas.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.paradas.dtos.LugarDTO;
import com.alvaro.empresas.passagens.paradas.dtos.LugarDtoUpdate;
import com.alvaro.empresas.passagens.paradas.services.CiudadService;
import com.alvaro.empresas.passagens.paradas.services.LugarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lugares")
public class LugarResource {
    @Autowired
    private LugarService lugarService;
    @Autowired
    private CiudadService ciudadService;

    @GetMapping
    public ResponseEntity<Page<LugarDTO>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok().body(lugarService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LugarDTO> getOne(@PathVariable(value = "id") Integer id) {
        var model = lugarService.findById(id);
        int idCiudad = model.getCiudad().getId();
        return ResponseEntity.ok().body(new LugarDTO(model, idCiudad));
    }

    @PostMapping
    public ResponseEntity<LugarDTO> save(@RequestBody @Valid LugarDTO dto) {
        var ciudad = ciudadService.findById(dto.idCiudad());
        var model = lugarService.save(dto, ciudad);
        int idCiudad = model.getCiudad().getId();
        return ResponseEntity.ok().body(new LugarDTO(model, idCiudad));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LugarDTO> update(@PathVariable(value = "id") Integer id, @RequestBody @Valid LugarDtoUpdate dto) {
        var model = lugarService.update(dto, id);
        int idCiudad = model.getCiudad().getId();
        return ResponseEntity.ok().body(new LugarDTO(model, idCiudad));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mensaje> delete(@PathVariable(value = "id") Integer id) {
        var model = lugarService.findById(id);
        lugarService.delete(model);
        return ResponseEntity.ok().body(new Mensaje("El lugar fue eliminado conexito"));
    }

}
