package com.alvaro.empresas.passagens.autobuses.resources;

import com.alvaro.empresas.passagens.autobuses.dtos.AutobusDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.AutobusDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.services.AutobusService;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationError;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/autobuses")
public class AutobusResource {
    @Autowired
    private AutobusService autobusService;

    @GetMapping
    public ResponseEntity<Page<AutobusDTO>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok().body(autobusService.findAll(pageable));
    }

    @GetMapping("/from/{idEmpresa}")
    public ResponseEntity<Page<AutobusDTO>> getAutobusesFromEmpresa(@PathVariable(value = "idEmpresa") Integer id,
                                                                    @PageableDefault(size = 10, sort = {"placa"}) Pageable pageable) {
        return ResponseEntity.ok().body(autobusService.findAllFromEmpresa(id, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutobusDTO> getOne(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok().body(autobusService.getOne(id));
    }

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody AutobusDTO dto, BindingResult bindingResult) {
        ValidationError validacao = autobusService.validar(bindingResult, dto);
        if (!validacao.getErrors().isEmpty()) {
            return ResponseEntity.unprocessableEntity().body(validacao);
        }
        return ResponseEntity.ok().body(autobusService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable(value = "id") Integer id, @Valid @RequestBody AutobusDTOUpdate dto, BindingResult bindingResult) {
        var transform = new AutobusDTO();
        transform.setPlaca(dto.placa());
        ValidationError validacao = autobusService.validar(bindingResult, transform);
        if (!validacao.getErrors().isEmpty()) {
            return ResponseEntity.unprocessableEntity().body(validacao);
        }
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
