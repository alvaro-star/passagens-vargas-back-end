package com.alvaro.empresas.passagens.paradas.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.paradas.dtos.LugarDTO;
import com.alvaro.empresas.passagens.paradas.dtos.LugarDtoUpdate;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.services.CiudadService;
import com.alvaro.empresas.passagens.paradas.services.LugarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/lugares")
public class LugarResource {
    @Autowired
    private LugarService lugarService;
    @Autowired
    private CiudadService ciudadService;

    @GetMapping
    public ResponseEntity<List<LugarDTO>> getAll() {
        List<LugarModel> models = lugarService.findAll();
        List<LugarDTO> dtos = new ArrayList<>();
        models.forEach(model -> {
            var dto = new LugarDTO(model);
            dto.setIdCiudad(model.getCiudad().getId());
            dtos.add(dto);
        });
        return ResponseEntity.ok().body(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LugarDTO> getOne(@PathVariable(value = "id") Integer id) {
        var model = lugarService.findById(id);
        var dto = new LugarDTO(model);
        dto.setIdCiudad(model.getCiudad().getId());
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<LugarDTO> save(@RequestBody @Valid LugarDTO dto) {
        var ciudad = ciudadService.findById(dto.getIdCiudad());
        var model = lugarService.save(dto, ciudad);
        var dtoResponse = new LugarDTO(model);
        dtoResponse.setIdCiudad(ciudad.getId());
        return ResponseEntity.ok().body(dtoResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LugarDTO> update(@PathVariable(value = "id") Integer id, @RequestBody @Valid LugarDtoUpdate dto) {
        var model = lugarService.update(dto, id);
        var dtoResponse = new LugarDTO(model);
        dtoResponse.setIdCiudad(model.getCiudad().getId());
        return ResponseEntity.ok().body(dtoResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mensaje> delete(@PathVariable(value = "id") Integer id) {
        var model = lugarService.findById(id);
        lugarService.delete(model);
        return ResponseEntity.ok().body(new Mensaje("El lugar fue eliminado conexito"));
    }

}
