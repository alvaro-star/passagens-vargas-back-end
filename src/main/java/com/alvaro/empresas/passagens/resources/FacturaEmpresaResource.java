package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.services.relatorios.RelatorioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/facturas")
@SecurityRequirement(name = "bearer-key")
public class FacturaEmpresaResource {
    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/{idEmpresa}")
    //@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<byte[]> getRelatorioByEmpresa(@PathVariable("idEmpresa") UUID idEmpresa, Model model) {
        Date data = new Date(2024, Calendar.SEPTEMBER, 15);
        byte[] relatorioPDF = relatorioService.makeRelatorioMensual(idEmpresa, data, model);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=relatorio.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(relatorioPDF, headers, HttpStatus.OK);
    }
}
