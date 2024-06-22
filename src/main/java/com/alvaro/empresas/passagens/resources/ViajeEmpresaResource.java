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
        if (user.hasRole("ROLE_ADMIN") || user.isMyEmpresa(idEmpresa)) {
            if (dto.idCiudadDestino() == null || dto.idCiudadDestino() == 0)
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
