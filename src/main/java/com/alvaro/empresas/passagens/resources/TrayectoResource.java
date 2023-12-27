package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.autobuses.services.AutobusService;
import com.alvaro.empresas.passagens.dtos.TrayectoDto;
import com.alvaro.empresas.passagens.models.TrayectoModel;
import com.alvaro.empresas.passagens.repositories.TrayectoRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trayectos")
public class TrayectoResource {
    @Autowired
    private TrayectoRepository trayectoRepository;
    @Autowired
    private AutobusService autobusService;

    @GetMapping("/{id}")
    public ResponseEntity<TrayectoDto> getOne(@PathVariable(value = "id") UUID id) {
        var optional = trayectoRepository.findById(id);
        var model = optional.orElseThrow(() -> new ObjectNotFoundException(id, TrayectoModel.class.getName()));
        var dto = new TrayectoDto(model);
        dto.setIdAutobus(model.getAutobus().getId());
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping
    public ResponseEntity<List<TrayectoDto>> getAll() {
        List<TrayectoModel> models = trayectoRepository.findAll();
        List<TrayectoDto> dtos = new ArrayList<>();
        models.forEach(model -> {
            var dto = new TrayectoDto(model);
            dto.setIdAutobus(model.getAutobus().getId());
            dtos.add(dto);
        });
        return ResponseEntity.ok().body(dtos);
    }

    /*@PostMapping//inconcluso
    public ResponseEntity<TrayectoDto> save(@RequestBody @Valid TrayectoDto dto) {
        //var autobus = autobusService.find
        var model = new TrayectoModel();

    }*/

}
