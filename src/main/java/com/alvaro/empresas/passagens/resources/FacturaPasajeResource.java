package com.alvaro.empresas.passagens.resources;


import com.alvaro.empresas.passagens.dtos.FacturaPasajeDTO;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.services.FacturaPasajeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/pagos")
@SecurityRequirement(name = "bearer-key")
public class FacturaPasajeResource {
    @Autowired
    private FacturaPasajeService facturaPasajeService;


    @GetMapping("/{idViaje}/from/viaje")
    public ResponseEntity<Page<FacturaPasajeDTO>> findAll(@PathVariable(value = "idViaje") UUID idViaje,
                                                          @PageableDefault(sort = "created_at", direction = Sort.Direction.DESC) Pageable pageable) {
        if (idViaje != null)
            return ResponseEntity.ok(facturaPasajeService.findAllFromViaje(idViaje, pageable));
        return ResponseEntity.badRequest().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Object> pagarQR(@PathVariable(value = "id") UUID id) {
        if (facturaPasajeService.pagarQr(id)) {
            return ResponseEntity.ok(new Mensaje("El pago fue hecho con exito"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mensaje("Falla ala hora del pago"));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> getFactura(@PathVariable(value = "id") UUID id) {
        byte[] pasajesPDF = facturaPasajeService.downloadFactura(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=pasajes.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(pasajesPDF, headers, HttpStatus.OK);
    }
}
