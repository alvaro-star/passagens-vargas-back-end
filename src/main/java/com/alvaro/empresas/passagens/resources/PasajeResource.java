package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTO;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTOVenta;
import com.alvaro.empresas.passagens.enums.MetodoPagamentoEnum;
import com.alvaro.empresas.passagens.services.PasajeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pasajes")
@SecurityRequirement(name = "bearer-key")
public class PasajeResource {
    @Autowired
    private PasajeService pasajeService;

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody PasajesDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pasajeService.save(dto, MetodoPagamentoEnum.QR, true, true));
    }

    //PreAuthorize("hasRole('ROLE_EMPRESA-FUNCIONARIO', 'ROLE_EMPRESA_ADMIN')")
    @PostMapping("/vender")
    public ResponseEntity<Object> vender(@Valid @RequestBody PasajesDTOVenta dto) {
        PasajesDTO dto1 = new PasajesDTO(dto);
        if (pasajeService.empresaValida(dto.idPrecio())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(pasajeService.save(dto1, dto.metodo(), false, false));
        } else {
            return ResponseEntity.unprocessableEntity().body(new Mensaje("No se puede vender el pasaje de otra empresa"));
        }
    }
}
