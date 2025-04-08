package com.alvaro.empresas.passagens.resources;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.alvaro.empresas.passagens.dtos.pasagens.CodigoPago;
import com.alvaro.empresas.passagens.dtos.pasagens.PassagemDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.pasagens.PassagensDTO;
import com.alvaro.empresas.passagens.dtos.pasagens.PassagensDTOVenta;
import com.alvaro.empresas.passagens.services.PassagemService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("passagens")
@SecurityRequirement(name = "bearer-key")
public class PassagemResource {
    @Autowired
    private PassagemService passagemService;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public PassagemDTOEmpresaResponse findById(@PathVariable UUID id) {
        return passagemService.findById(id);
    }

    @GetMapping("{id}/download")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getPassagemPdf(@PathVariable UUID id) {
        byte[] pasajePdf = passagemService.getPassagemPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=pasaje.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(pasajePdf, headers, HttpStatus.OK);
    }

    // Venta de pasajes al público
    @PostMapping("comprar")
    @ResponseStatus(HttpStatus.CREATED)
    public Object save(@RequestBody @Valid PassagensDTO dto) {
        return passagemService.saveCliente(dto);
    }

    @PostMapping("vender")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_FUNCIONARIO', 'ROLE_EMPRESA_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CodigoPago vender(@RequestBody @Valid PassagensDTOVenta dto) {
        var idPago = passagemService.saveEmpresa(dto);
        return new CodigoPago(idPago);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_FUNCIONARIO', 'ROLE_EMPRESA_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rembolso(@PathVariable UUID id) {
        passagemService.delete(id);
    }
}
