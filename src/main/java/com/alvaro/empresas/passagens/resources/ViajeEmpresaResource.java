package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.autobuses.services.AutobusService;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacaoEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacaoFromAutobus;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOForm;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOListBusquedaEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOUpdate;
import com.alvaro.empresas.passagens.helpers.beans.MyUserService;
import com.alvaro.empresas.passagens.security.models.RoleList;
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
    private final AutobusService autobusService;

    @Autowired
    public ViajeEmpresaResource(ViajeEmpresaService viajeEmpresaService, MyUserService myUserService, AutobusService autobusService) {
        this.viajeEmpresaService = viajeEmpresaService;
        this.myUserService = myUserService;
        this.autobusService = autobusService;
    }


    @GetMapping("/from/{idEmpresa}/{type}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Page<ViajeDTOListBusquedaEmpresa>> getAllFromEmpresa(@PathVariable(value = "idEmpresa") UUID id,
                                                                               @PageableDefault(sort = "dataHoraSalida", direction = Sort.Direction.DESC) Pageable pageable,
                                                                               @PathVariable(value = "type") String type) {
        return ResponseEntity.ok(viajeEmpresaService.findAllEmpresa(id, pageable, type));
    }

    @PostMapping("/from/autobus")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Page<ViajeDTOListBusquedaEmpresa>> getAllFromAutobus(@RequestBody @Valid ViajeDTOSolicitacaoFromAutobus solicitacao,
                                                                               @PageableDefault(sort = "dataHoraSalida") Pageable pageable) {
        var autobusModel = autobusService.findById(solicitacao.idAutobus());
        var usuarioLogado = myUserService.getUser();
        if (usuarioLogado.hasRole(RoleList.ROLE_ADMIN.toString()) || usuarioLogado.isMyEmpresa(autobusModel.getEmpresa().getId()))
            return ResponseEntity.ok(viajeEmpresaService.findAllFromAutobus(autobusModel, solicitacao, pageable));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/{idEmpresa}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<List<ViajeDTOListBusquedaEmpresa>> getViajeFromDia(@PathVariable(value = "idEmpresa") UUID idEmpresa,
                                                                             @RequestBody @Valid ViajeDTOSolicitacaoEmpresa dto) {
        var user = myUserService.getUser();
        if (user.hasRole(RoleList.ROLE_ADMIN.toString()) || user.isMyEmpresa(idEmpresa)) {
            if (dto.idCiudadDestino() == null || dto.idCiudadDestino() == 0) {
                return ResponseEntity.ok(viajeEmpresaService.getViajesFromSalida(idEmpresa, dto));
            } else
                return ResponseEntity.ok(viajeEmpresaService.getViajesFromDia(idEmpresa, dto));
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Object> save(@Valid @RequestBody ViajeDTOForm dto) {
        var autobus = autobusService.findById(dto.idAutobus());
        var user = myUserService.getUser();
        if (!autobus.isEnable())
            return ResponseEntity.badRequest().body(new Mensaje("El autobus esta inhabilitado"));
        if (autobus.getEmpresa().getBloqued() || !autobus.getEmpresa().getEnabled())
            return ResponseEntity.badRequest().body(new Mensaje("La empresa esta inhabilitada"));
        if (!user.isMyEmpresa(autobus.getEmpresa().getId()))
            return ResponseEntity.badRequest().body(new Mensaje("Este autobus no esta relacionado con esta empresa"));
        ViajeDTOEmpresaResponse response = viajeEmpresaService.save(dto, autobus);
        if (response == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje("Las paradas no son validas"));
        else
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Object> update(@PathVariable(value = "id") UUID id, @RequestBody @Valid ViajeDTOUpdate dto) {
        var viajeModel = viajeEmpresaService.findById(id);
        var user = myUserService.getUser();

        if (viajeModel.getEmpresa().getBloqued() || !viajeModel.getEmpresa().getEnabled())
            return ResponseEntity.badRequest().body(new Mensaje("La empresa esta inhabilitada"));
        if (!user.isMyEmpresa(viajeModel.getEmpresa().getId()))
            return ResponseEntity.badRequest().body(new Mensaje("El viaje no esta relacionado con esta empresa"));
        var autobusNuevo = autobusService.findById(dto.idAutobus());
        if (!autobusNuevo.getEmpresa().getId().equals(viajeModel.getEmpresa().getId()))
            return ResponseEntity.badRequest().body(new Mensaje("Este autobus le pertenece a otra empresa"));
        if (!autobusNuevo.isEnable())
            return ResponseEntity.badRequest().body(new Mensaje("El nuevo autobus esta inhabilitado"));
        return ResponseEntity.ok(viajeEmpresaService.update(dto, viajeModel, autobusNuevo));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") UUID id) {
        var model = viajeEmpresaService.findById(id);
        var user = myUserService.getUser();
        if (!user.isMyEmpresa(model.getEmpresa().getId()))
            return ResponseEntity.badRequest().body(new Mensaje("El viaje no pertenece a esta empresa"));
        if (model.getEmpresa().getBloqued() || !model.getEmpresa().getEnabled())
            return ResponseEntity.badRequest().body(new Mensaje("La empresa esta deshabilitada"));
        if (viajeEmpresaService.hasPasajes(model.getPrecios()))
            return ResponseEntity.badRequest().body(new Mensaje("El viaje ya posse un pasaje registrado"));

        viajeEmpresaService.delete(model);
        return ResponseEntity.noContent().build();
    }
}
