package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.pasagens.CodigoPago;
import com.alvaro.empresas.passagens.dtos.pasagens.PasagemDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.pasagens.PaagensDTO;
import com.alvaro.empresas.passagens.dtos.pasagens.PasagensDTOVenta;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.helpers.validators.EmpresaEnabled;
import com.alvaro.empresas.passagens.services.PasajeService;
import com.alvaro.empresas.passagens.services.PrecioService;
import com.alvaro.empresas.passagens.services.ViajeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pasajes")
@SecurityRequirement(name = "bearer-key")
public class PasajeResource {
    @Autowired
    private PasajeService pasajeService;
    @Autowired
    private UserLoguedComponent userLogued;
    @Autowired
    private ViajeService viajeService;
    @Autowired
    private PrecioService precioService;
    @Autowired
    private EmpresaEnabled empresaEnabled;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PasagemDTOEmpresaResponse getOne(@PathVariable UUID id) {
        return pasajeService.getOne(id);
    }

    @GetMapping("/{id}/download")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getFilePasaje(@PathVariable UUID id) {
        byte[] pasajePdf = pasajeService.getOnePasajeDownload(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=pasaje.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(pasajePdf, headers, HttpStatus.OK);
    }

    @GetMapping("/from/{idPrecio}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_FUNCIONARIO', 'ROLE_EMPRESA_ADMIN', 'ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public List<PasagemDTOEmpresaResponse> getPasajerosFromPrecio(@PathVariable UUID idPrecio) {
        var precio = precioService.findById(idPrecio);
        userLogued.validIfIsAdminOrOwnerEmpresa(precio.getEmpresaId());
        return pasajeService.getPasajesFromPrecio(idPrecio);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Object save(@RequestBody @Valid PaagensDTO dto, BindingResult bindingResult) { // Venta de pasajes al público
        return pasajeService.saveCliente(dto, bindingResult);
    }

    @PostMapping("/vender")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_FUNCIONARIO', 'ROLE_EMPRESA_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CodigoPago vender(@RequestBody @Valid PasagensDTOVenta dto, BindingResult bindingResult) {
        var viaje = viajeService.findById(dto.idViaje());
        userLogued.validIfIsMyEmpresa(viaje.getEmpresaId());
        empresaEnabled.validEmpresaEnabled(viaje.getEmpresaId());

        var idPago = pasajeService.saveEmpresa(dto, viaje, bindingResult);
        return new CodigoPago(idPago);
    }

    @DeleteMapping("/{id}") // Habilitado solo para el reembolso fijo
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_FUNCIONARIO', 'ROLE_EMPRESA_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rembolso(@PathVariable UUID id) {
        pasajeService.delete(id);
    }
}

