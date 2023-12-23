package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.LayoutBusDTO;
import com.alvaro.empresas.passagens.models.LayoutBusModel;
import com.alvaro.empresas.passagens.services.LayoutBusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/layout")
public class LayoutBusResource {
    @Autowired
    private LayoutBusService layoutBusService;

    @PostMapping
    public ResponseEntity<LayoutBusModel> save(@RequestBody @Valid LayoutBusDTO dto) {
        var model = layoutBusService.save(dto);
        model = layoutBusService.findById(model.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }
}
