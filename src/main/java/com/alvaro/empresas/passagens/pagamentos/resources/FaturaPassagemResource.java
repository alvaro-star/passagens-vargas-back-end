package com.alvaro.empresas.passagens.pagamentos.resources;


import com.alvaro.empresas.passagens.dtos.FaturaPasajeDTO;
import com.alvaro.empresas.passagens.pagamentos.services.FaturaPassagemService;
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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/pagamentos")
@SecurityRequirement(name = "bearer-key")
public class FaturaPassagemResource {
    @Autowired
    private FaturaPassagemService faturaPassagemService;

    @GetMapping("/{idViagem}/from/viagem")
    @ResponseStatus(HttpStatus.OK)
    public Page<FaturaPasajeDTO> findAll(@PathVariable UUID idViagem,
                                         @PageableDefault(sort = "created_at", direction = Sort.Direction.DESC) Pageable pageable) {
        return faturaPassagemService.findAllFromViagem(idViagem, pageable);
    }

    @PostMapping("/{id}/pagar-qr")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void pagarQR(@PathVariable UUID id) {
        faturaPassagemService.pagarQr(id);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> getFactura(@PathVariable UUID id) {
        byte[] pasajesPDF = faturaPassagemService.downloadFatura(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=pasajes.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(pasajesPDF, headers, HttpStatus.OK);
    }
}
