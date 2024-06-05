package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTO;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTOVenta;
import com.alvaro.empresas.passagens.enums.MetodoPagamentoEnum;
import com.alvaro.empresas.passagens.helpers.beans.MyUserService;
import com.alvaro.empresas.passagens.services.PasajeService;
import com.alvaro.empresas.passagens.services.ViajeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pasajes")
@SecurityRequirement(name = "bearer-key")
public class PasajeResource {
    private final PasajeService pasajeService;
    private final MyUserService myUserService;
    private final ViajeService viajeService;

    @Autowired
    public PasajeResource(PasajeService pasajeService, MyUserService myUserService, ViajeService viajeService) {
        this.pasajeService = pasajeService;
        this.myUserService = myUserService;
        this.viajeService = viajeService;
    }

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody PasajesDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pasajeService.save(dto, MetodoPagamentoEnum.QR, true, true));
    }

    @PostMapping("/vender")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_FUNCIONARIO', 'ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> vender(@Valid @RequestBody PasajesDTOVenta dto) {
        var usuario = myUserService.getUser();
        var viaje = viajeService.findById(dto.idViaje());
        if (!usuario.isMyEmpresa(viaje.getEmpresa().getId()))
            return ResponseEntity.unprocessableEntity().body(new Mensaje("No se puede vender el pasaje de otra empresa"));
        return ResponseEntity.status(HttpStatus.CREATED).body(pasajeService.saveEmpresa(dto, dto.metodo(),viaje, false, false));
    }
}
