package com.alvaro.empresas.passagens.lugares.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.lugares.dtos.CiudadDTO;
import com.alvaro.empresas.passagens.lugares.dtos.CiudadDtoUpdate;
import com.alvaro.empresas.passagens.lugares.models.CiudadModel;
import com.alvaro.empresas.passagens.lugares.services.CiudadService;
import com.alvaro.empresas.passagens.lugares.services.DepartamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ciudades")
public class CiudadResource {
    @Autowired
    private CiudadService ciudadService;
    @Autowired
    private DepartamentoService departamentoService;

    @GetMapping
    public ResponseEntity<List<CiudadDTO>> getAll() {//Incompleto
        List<CiudadModel> models = ciudadService.findAll();
        List<CiudadDTO> dtos = new ArrayList<>();
        models.forEach(model -> {
            var dto = new CiudadDTO(model);
            dto.setIdDepartamento(model.getDepartamento().getId());
            dtos.add(dto);
        });
        return ResponseEntity.ok().body(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CiudadDTO> getOne(@PathVariable(value = "id") Integer id) {
        var model = ciudadService.findById(id);
        var dto = new CiudadDTO(model);
        dto.setIdDepartamento(model.getDepartamento().getId());
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<CiudadDTO> save(@RequestBody @Valid CiudadDTO dto) {
        var departamento = departamentoService.findById(dto.getIdDepartamento());
        var model = ciudadService.save(dto, departamento);
        var dtoResponse = new CiudadDTO(model);
        dtoResponse.setIdDepartamento(model.getDepartamento().getId());
        return ResponseEntity.ok().body(dtoResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CiudadDTO> update(@RequestBody @Valid CiudadDtoUpdate dto, @PathVariable(value = "id") Integer id) {
        var model = ciudadService.update(dto, id);
        var dtoResponse = new CiudadDTO(model);
        dtoResponse.setIdDepartamento(model.getDepartamento().getId());
        return ResponseEntity.ok().body(dtoResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") Integer id) {
        var model = ciudadService.findById(id);
        ciudadService.delete(model);
        return ResponseEntity.badRequest().body(new Mensaje("La ciudad fue eliminada con exito"));
    }


}
