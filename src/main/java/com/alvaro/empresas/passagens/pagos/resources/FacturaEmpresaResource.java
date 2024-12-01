package com.alvaro.empresas.passagens.pagos.resources;

import com.alvaro.empresas.passagens.pagos.dtos.RelatorioSolicitudDTO;
import com.alvaro.empresas.passagens.services.relatorios.RelatorioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/facturas")
@SecurityRequirement(name = "bearer-key")
public class FacturaEmpresaResource {
    @Autowired
    private RelatorioService relatorioService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<byte[]> getRelatorioByEmpresa(@RequestBody @Valid RelatorioSolicitudDTO solicitudDTO) {
        byte[] relatorioPDF = relatorioService.makeRelatorioMensual(solicitudDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=relatorio.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(relatorioPDF, headers, HttpStatus.OK);
    }
}
