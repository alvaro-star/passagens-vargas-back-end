package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.EmpresaDto;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.services.EmpresaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/empresas")
@SecurityRequirement(name = "bearer-key")
public class EmpresaResource {

    @Autowired
    private EmpresaService empresaService;


    @GetMapping
    public ResponseEntity<Page<EmpresaDto>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(empresaService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDto> getOne(@PathVariable(value = "id") UUID id) {
        EmpresaModel model = empresaService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new EmpresaDto(model));
    }

    @PostMapping
    public ResponseEntity<EmpresaDto> save(@RequestBody @Valid EmpresaDto dto) {
        EmpresaModel model = empresaService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EmpresaDto(model));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaDto> update(@PathVariable(value = "id") UUID id, @RequestBody @Valid EmpresaDto dto) {
        EmpresaModel model = empresaService.update(dto, id);
        return ResponseEntity.status(HttpStatus.OK).body(new EmpresaDto(model));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") UUID id) {
        empresaService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(new Mensaje("Eliminado"));
    }
}
