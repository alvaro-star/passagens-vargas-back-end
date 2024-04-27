package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOListBusquedaEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacao;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOForm;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOList;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOUpdate;
import com.alvaro.empresas.passagens.services.ViajeEmpresaService;
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
@RequestMapping("/empresa/viajes")
@SecurityRequirement(name = "bearer-key")
//PreAuthorize("hasRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN')")
public class ViajeEmpresaResource {
    @Autowired
    private ViajeEmpresaService viajeEmpresaService;

    @GetMapping
    public ResponseEntity<Page<ViajeDTOList>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(viajeEmpresaService.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<List<ViajeDTOListBusquedaEmpresa>> getViajeFromDia(@RequestBody @Valid ViajeDTOSolicitacao dto) {
        return ResponseEntity.ok(viajeEmpresaService.getViajesFromDia(dto));
    }

    @PostMapping("/create")
    public ResponseEntity<Object> save(@Valid @RequestBody ViajeDTOForm dto) {
        ViajeDTOEmpresaResponse response = viajeEmpresaService.save(dto);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje("Las paradas no son validas"));
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<ViajeDTOUpdate> update(@PathVariable(value = "id") UUID id, @RequestBody @Valid ViajeDTOUpdate dto) {
        return ResponseEntity.ok(viajeEmpresaService.update(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") UUID id) {
        var model = viajeEmpresaService.findById(id);


        if (!model.getPrecios().isEmpty())
            return ResponseEntity.badRequest().body(new Mensaje("El trayecto tiene precios associados"));

        if (!model.getPagos().isEmpty())
            return ResponseEntity.badRequest().body(new Mensaje("El trayecto tiene pagos associados"));

        if (!model.getParadas().isEmpty())
            return ResponseEntity.badRequest().body(new Mensaje("El trayecto tiene paradas associados"));

        viajeEmpresaService.delete(model);
        return ResponseEntity.noContent().build();
    }
}
