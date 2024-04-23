package com.alvaro.empresas.passagens.resources;


import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.services.PagoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/pagos")
@SecurityRequirement(name = "bearer-key")
public class PagoResource {
    @Autowired
    private PagoService pagoService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> pagarQR(@PathVariable(value = "id") UUID id) {

        if (pagoService.pagarQr(id)) {
            return ResponseEntity.ok(new Mensaje("El pago fue hecho con exito"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mensaje("Falla ala hora del pago"));
    }
}
