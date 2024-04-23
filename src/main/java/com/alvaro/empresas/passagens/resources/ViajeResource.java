package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOListBusqueda;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacao;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTO;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOList;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOResponse;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOUpdate;
import com.alvaro.empresas.passagens.services.ViajeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/viajes")
@SecurityRequirement(name = "bearer-key")
public class ViajeResource {
    @Autowired
    private ViajeService viajeService;

    @GetMapping
    public ResponseEntity<Page<ViajeDTOList>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(viajeService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViajeDTOResponse> getOne(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(viajeService.getOne(id));
    }

    //, @PageableDefault(size = 30) Pageable pageable
    @PostMapping
    public ResponseEntity<List<ViajeDTOListBusqueda>> getViajeFromDia(@RequestBody @Valid ViajeDTOSolicitacao dto) {
        List<ViajeDTOListBusqueda> viajes = viajeService.getViajesFromDia(dto);
        return ResponseEntity.ok(viajes);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> save(@Valid @RequestBody ViajeDTO dto) {
        ViajeDTOResponse response = viajeService.save(dto);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje("Las paradas no son validas"));
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViajeDTOList> update(@PathVariable(value = "id") UUID id, @RequestBody @Valid ViajeDTOUpdate dto) {
        return ResponseEntity.ok(viajeService.update(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") UUID id) {
        var model = viajeService.findById(id);


        if (!model.getPrecios().isEmpty()) {
            return ResponseEntity.badRequest().body(new Mensaje("El trayecto tiene precios associados"));
        }
        if (!model.getPagos().isEmpty()) {
            return ResponseEntity.badRequest().body(new Mensaje("El trayecto tiene pagos associados"));
        }
        if (!model.getParadas().isEmpty()) {
            return ResponseEntity.badRequest().body(new Mensaje("El trayecto tiene paradas associados"));
        }

        viajeService.delete(model);
        return ResponseEntity.noContent().build();
    }
}
