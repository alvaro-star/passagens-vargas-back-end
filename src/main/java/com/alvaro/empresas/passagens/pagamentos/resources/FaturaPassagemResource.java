package com.alvaro.empresas.passagens.pagamentos.resources;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.alvaro.empresas.passagens.pagamentos.services.FaturaPassagemService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@RestController
@RequestMapping("pagamentos")
@SecurityRequirement(name = "bearer-key")
public class FaturaPassagemResource {
    @Autowired
    private FaturaPassagemService faturaPassagemService;

    @PostMapping("{id}/pagar-qr")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void pagarQR(@PathVariable UUID id) {
        faturaPassagemService.pagarQr(id);
    }

    @GetMapping("{id}/download")
    public ResponseEntity<byte[]> getFatura(@PathVariable UUID id) {
        byte[] pasajesPDF = faturaPassagemService.downloadFatura(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=pasajes.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(pasajesPDF, headers, HttpStatus.OK);
    }
}
