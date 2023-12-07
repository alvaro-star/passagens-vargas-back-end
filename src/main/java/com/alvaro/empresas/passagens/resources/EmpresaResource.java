package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.EmpresaDto;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.services.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empresas")
public class EmpresaResource {

    @Autowired
    private EmpresaService empresaService;


    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDto> getOne(@PathVariable(value = "id") Integer id) {
        EmpresaModel model = empresaService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new EmpresaDto(model));
    }

    @PostMapping()
    public ResponseEntity<EmpresaDto> save(@RequestBody @Valid EmpresaDto dto) {
        EmpresaModel model = empresaService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EmpresaDto(model));
    }

}
