package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.onibus.services.AutobusService;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacaoEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacaoFromAutobus;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacaoFromEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOCreate;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOFormCopy;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOListBusquedaEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOUpdate;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.services.ViajeEmpresaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
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
    private UserLoguedComponent userLogued;
    @Autowired
    private AutobusService autobusService;

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Object> getPdfFromViaje(@PathVariable("id") UUID idViaje) {
        byte[] viajeRelatorio = viajeEmpresaService.getPdfFromViaje(idViaje);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=viaje.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(viajeRelatorio, headers, HttpStatus.OK);
    }

    @PostMapping("/from/empresa")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Page<ViajeDTOListBusquedaEmpresa>> getAllFromEmpresaBetweenMonth(@RequestBody @Valid ViajeDTOSolicitacaoFromEmpresa solicitacao,
                                                                                           @PageableDefault(sort = "dataHoraSalida") Pageable pageable) {
        userLogued.validIfIsAdminOrOwnerEmpresa(solicitacao.idEmpresa());
        return ResponseEntity.ok(viajeEmpresaService.findAllByEmpresaBetweenDates(solicitacao, pageable));
    }

    @PostMapping("/from/autobus")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Page<ViajeDTOListBusquedaEmpresa>> getAllFromAutobus(@RequestBody @Valid ViajeDTOSolicitacaoFromAutobus solicitacao,
                                                                               @PageableDefault(sort = "dataHoraSalida") Pageable pageable) {
        var autobusModel = autobusService.findById(solicitacao.idAutobus());
        userLogued.validIfIsAdminOrOwnerEmpresa(autobusModel.getEmpresaId());
        return ResponseEntity.ok(viajeEmpresaService.findAllFromAutobus(autobusModel, solicitacao, pageable));
    }

    @PostMapping("/{idEmpresa}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<List<ViajeDTOListBusquedaEmpresa>> getViajeFromDia(@PathVariable(value = "idEmpresa") UUID idEmpresa,
                                                                             @RequestBody @Valid ViajeDTOSolicitacaoEmpresa dto) {
        userLogued.validIfIsAdminOrOwnerEmpresa(idEmpresa);
        if (dto.idCiudadDestino() == null || dto.idCiudadDestino() == 0)
            return ResponseEntity.ok(viajeEmpresaService.getViajesFromSalida(idEmpresa, dto));
        else
            return ResponseEntity.ok(viajeEmpresaService.getViajesFromDia(idEmpresa, dto));

    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Object> save(@Valid @RequestBody ViajeDTOCreate dto) {
        var autobus = autobusService.findById(dto.idAutobus());

        userLogued.validIfIsMyEmpresa(autobus.getEmpresaId());
        ViajeDTOEmpresaResponse response = viajeEmpresaService.save(dto, autobus);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/create/copy")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Object> saveViajesCopyFromDay(@RequestBody @Valid ViajeDTOFormCopy dto) {
        var viaje = viajeEmpresaService.findById(dto.idViaje());

        userLogued.validIfIsMyEmpresa(viaje.getEmpresaId());

        viajeEmpresaService.saveOneCopy(dto, viaje);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Object> update(@PathVariable UUID id, @RequestBody @Valid ViajeDTOUpdate dto) {
        var viajeModel = viajeEmpresaService.findById(id);

        userLogued.validIfIsMyEmpresa(viajeModel.getEmpresaId());
        return ResponseEntity.ok(viajeEmpresaService.update(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Object> delete(@PathVariable UUID id) {
        var model = viajeEmpresaService.findById(id);

        userLogued.validIfIsMyEmpresa(model.getEmpresaId());
        viajeEmpresaService.delete(model);
        return ResponseEntity.noContent().build();
    }
}
