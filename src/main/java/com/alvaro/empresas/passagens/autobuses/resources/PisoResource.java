package com.alvaro.empresas.passagens.autobuses.resources;

import com.alvaro.empresas.passagens.autobuses.dtos.AsientoBloqueadoDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.PisoDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.PisoDtoUpdate;
import com.alvaro.empresas.passagens.autobuses.models.AsientoBloqueadoModel;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.services.AsientoBloqueadosService;
import com.alvaro.empresas.passagens.autobuses.services.AutobusService;
import com.alvaro.empresas.passagens.autobuses.services.PisoService;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/layout")
public class PisoResource {
    @Autowired
    private PisoService pisoService;
    @Autowired
    private AsientoBloqueadosService asientoBloqueadosService;
    @Autowired
    private AutobusService autobusService;

    @GetMapping("/{id}")
    public ResponseEntity<PisoDTO> getOne(@PathVariable(value = "id") Integer id) {
        var model = pisoService.findById(id);
        var dto = new PisoDTO(model);

        for (AsientoBloqueadoModel asientoModel : model.getAsientosBloqueados()) {
            dto.getAsientosBloqueados().add(new AsientoBloqueadoDTO(asientoModel));
        }
        dto.setIdAutobus(model.getAutobus().getId());

        return ResponseEntity.ok().body(dto);

    }

    @GetMapping
    public ResponseEntity<List<PisoDTO>> findAll() {
        List<PisoModel> models = pisoService.findAll();
        List<PisoDTO> dtos = new ArrayList<>();

        for (PisoModel model : models) {
            var dto = new PisoDTO(model);
            dto.setIdAutobus(model.getAutobus().getId());
            for (AsientoBloqueadoModel bloqueado : model.getAsientosBloqueados()) {
                dto.getAsientosBloqueados().add(new AsientoBloqueadoDTO(bloqueado));
            }
            dtos.add(dto);
        }

        return ResponseEntity.ok().body(dtos);
    }


    @PostMapping
    public ResponseEntity<PisoDTO> save(@RequestBody @Valid PisoDTO dto) {
        var autobus = autobusService.findById(dto.getIdAutobus());
        var model = pisoService.save(dto, autobus);
        var dtoSave = new PisoDTO(model);
        List<AsientoBloqueadoDTO> bloqueadoDTOS = asientoBloqueadosService.convertModelsToDtos(model.getAsientosBloqueados());
        dtoSave.setIdAutobus(autobus.getId());
        dtoSave.setAsientosBloqueados(bloqueadoDTOS);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoSave);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PisoDTO> update(@PathVariable(value = "id") Integer id, @RequestBody @Valid PisoDtoUpdate dto) {
        var model = pisoService.update(dto, id);
        var dtoSave = new PisoDTO(model);
        List<AsientoBloqueadoDTO> bloqueadoDTOS = asientoBloqueadosService.convertModelsToDtos(model.getAsientosBloqueados());
        dtoSave.setAsientosBloqueados(bloqueadoDTOS);
        dtoSave.setIdAutobus(model.getAutobus().getId());
        return ResponseEntity.ok().body(dtoSave);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") Integer id) {
        pisoService.delete(id);
        return ResponseEntity.ok().body(new Mensaje("Eliminado"));

    }
}
