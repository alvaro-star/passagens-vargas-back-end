package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacaoEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOForm;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOListBusquedaEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOUpdate;
import com.alvaro.empresas.passagens.helpers.beans.MyUserService;
import com.alvaro.empresas.passagens.services.ViajeEmpresaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final ViajeEmpresaService viajeEmpresaService;
    private final MyUserService myUserService;

    @Autowired
    public ViajeEmpresaResource(ViajeEmpresaService viajeEmpresaService, MyUserService myUserService) {
        this.viajeEmpresaService = viajeEmpresaService;
        this.myUserService = myUserService;
    }

    @GetMapping("/from/{idEmpresa}/{type}")
    public ResponseEntity<Page<ViajeDTOListBusquedaEmpresa>> getAllFromEmpresa(@PathVariable(value = "idEmpresa") UUID id,
                                                                               @PageableDefault(sort = "dataHoraSalida", direction = Sort.Direction.DESC) Pageable pageable,
                                                                               @PathVariable(value = "type") String type) {
        return ResponseEntity.ok(viajeEmpresaService.findAllEmpresa(id, pageable, type));
    }

    @PostMapping("/{idEmpresa}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<List<ViajeDTOListBusquedaEmpresa>> getViajeFromDia(@PathVariable(value = "idEmpresa") UUID idEmpresa,
                                                                             @RequestBody @Valid ViajeDTOSolicitacaoEmpresa dto) {
        var user = myUserService.getUser();
        if (user.hasRole("ROLE_ADMIN") || user.isMyEmpresa(idEmpresa)) {
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
        if (response == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje("Las paradas no son validas"));
        else
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
