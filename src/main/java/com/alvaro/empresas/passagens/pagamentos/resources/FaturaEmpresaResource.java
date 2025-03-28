package com.alvaro.empresas.passagens.pagamentos.resources;

import com.alvaro.empresas.passagens.pagamentos.dtos.RelatorioSolicitacaoDTO;
import com.alvaro.empresas.passagens.pagamentos.services.relatorios.RelatorioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/faturas")
@SecurityRequirement(name = "bearer-key")
public class FaturaEmpresaResource {
    @Autowired
    private RelatorioService relatorioService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<byte[]> getRelatorioByEmpresa(@RequestBody @Valid RelatorioSolicitacaoDTO solicitudDTO) {
        byte[] relatorioPDF = relatorioService.makeRelatorioMensal(solicitudDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=relatorio.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(relatorioPDF, headers, HttpStatus.OK);
    }
}
