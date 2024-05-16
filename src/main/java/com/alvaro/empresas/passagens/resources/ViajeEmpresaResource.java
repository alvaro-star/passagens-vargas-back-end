package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacaoEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOListBusquedaEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacao;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOForm;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOList;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOUpdate;
import com.alvaro.empresas.passagens.helpers.beans.MyUserService;
import com.alvaro.empresas.passagens.services.ViajeEmpresaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/empresa/viajes")
@SecurityRequirement(name = "bearer-key")
public class ViajeEmpresaResource {
    @Autowired
    private ViajeEmpresaService viajeEmpresaService;

    @Autowired
    private MyUserService myUserService;

    @GetMapping
    public ResponseEntity<Page<ViajeDTOList>> getAll(Pageable pageable) {
        return ResponseEntity.ok(viajeEmpresaService.findAll(pageable));
    }

    @GetMapping("/from/{idEmpresa}")
    public ResponseEntity<Page<ViajeDTOList>> getAllFromEmpresa(@PathVariable(value = "idEmpresa") UUID id, Pageable pageable) {
        return ResponseEntity.ok(viajeEmpresaService.findAllEmpresa(id, pageable));
    }

    @PostMapping("/{idEmpresa}")
    public ResponseEntity<List<ViajeDTOListBusquedaEmpresa>> getViajeFromDia(@PathVariable(value = "idEmpresa") UUID idEmpresa,
                                                                             @RequestBody @Valid ViajeDTOSolicitacaoEmpresa dto) {
        var user = myUserService.getUser();
        boolean isAdmin = false;
        for (String role : user.roles())
            isAdmin = role.equals("ROLE_ADMIN");

        if (isAdmin) {
            if (dto.idCiudadDestino() == null)
                return ResponseEntity.ok(viajeEmpresaService.getViajesFromSalida(idEmpresa, dto));
            else
                return ResponseEntity.ok(viajeEmpresaService.getViajesFromDia(idEmpresa, dto));
        }

        if (user.idEmpresa() != null && user.idEmpresa() == idEmpresa) {
            if (dto.idCiudadDestino() == null)
                return ResponseEntity.ok(viajeEmpresaService.getViajesFromSalida(idEmpresa, dto));
            else
                return ResponseEntity.ok(viajeEmpresaService.getViajesFromDia(idEmpresa, dto));
        }

        return ResponseEntity.badRequest().build();
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
