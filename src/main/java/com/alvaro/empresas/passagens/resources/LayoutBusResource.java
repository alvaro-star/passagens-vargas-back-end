package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.AsientoBloqueadoDTO;
import com.alvaro.empresas.passagens.dtos.LayoutBusDTO;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.models.AsientoBloqueadoModel;
import com.alvaro.empresas.passagens.models.LayoutBusModel;
import com.alvaro.empresas.passagens.services.AsientoBloqueadosService;
import com.alvaro.empresas.passagens.services.LayoutBusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/layout")
public class LayoutBusResource {
    @Autowired
    private LayoutBusService layoutBusService;
    @Autowired
    private AsientoBloqueadosService asientoBloqueadosService;

    @GetMapping("/{id}")
    public ResponseEntity<LayoutBusDTO> getOne(@PathVariable(value = "id") Integer id) {
        var model = layoutBusService.findById(id);
        var dto = new LayoutBusDTO(model);

        for (AsientoBloqueadoModel asientoModel : model.getAsientosBloqueados()) {
            dto.getAsientosBloqueados().add(new AsientoBloqueadoDTO(asientoModel));
        }

        return ResponseEntity.ok().body(dto);

    }

    @GetMapping
    public ResponseEntity<List<LayoutBusDTO>> findAll() {
        List<LayoutBusModel> models = layoutBusService.findAll();
        List<LayoutBusDTO> dtos = new ArrayList<>();

        for (LayoutBusModel model : models) {
            var dto = new LayoutBusDTO(model);
            for (AsientoBloqueadoModel bloqueado : model.getAsientosBloqueados()) {
                dto.getAsientosBloqueados().add(new AsientoBloqueadoDTO(bloqueado));
            }
            dtos.add(dto);
        }

        return ResponseEntity.ok().body(dtos);
    }


    @PostMapping
    public ResponseEntity<LayoutBusDTO> save(@RequestBody @Valid LayoutBusDTO dto) {
        var model = layoutBusService.save(dto);
        var dtoSave = new LayoutBusDTO(model);
        List<AsientoBloqueadoDTO> bloqueadoDTOS = asientoBloqueadosService.convertModelsToDtos(model.getAsientosBloqueados());
        dtoSave.setAsientosBloqueados(bloqueadoDTOS);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoSave);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LayoutBusDTO> update(@PathVariable(value = "id") Integer id, @RequestBody @Valid LayoutBusDTO dto) {
        var model = layoutBusService.update(dto, id);
        var dtoSave = new LayoutBusDTO(model);
        List<AsientoBloqueadoDTO> bloqueadoDTOS = asientoBloqueadosService.convertModelsToDtos(model.getAsientosBloqueados());
        dtoSave.setAsientosBloqueados(bloqueadoDTOS);
        return ResponseEntity.ok().body(dtoSave);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") Integer id) {
        layoutBusService.delete(id);
        return ResponseEntity.ok().body(new Mensaje("Eliminado"));

    }
}
