package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.pasajes.CodigoPago;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajeDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTO;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTOVenta;
import com.alvaro.empresas.passagens.helpers.Mensaje;
import com.alvaro.empresas.passagens.helpers.beans.MyUserComponent;
import com.alvaro.empresas.passagens.helpers.validators.EmpresaEnabled;
import com.alvaro.empresas.passagens.services.PasajeService;
import com.alvaro.empresas.passagens.services.PrecioService;
import com.alvaro.empresas.passagens.services.ViajeService;
import com.alvaro.empresas.passagens.services.validacao.ValidarCompraPasajes;
import com.alvaro.empresas.passagens.services.validacao.ValidationErrorsWithList;
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
    private MyUserComponent myUserComponent;
    @Autowired
    private ViajeService viajeService;
    @Autowired
    private PrecioService precioService;
    @Autowired
    private EmpresaEnabled empresaEnabled;

    @GetMapping("/{id}")
    public ResponseEntity<PasajeDTOEmpresaResponse> getOne(@PathVariable UUID id) {
        var model = pasajeService.findById(id);
        return ResponseEntity.ok(new PasajeDTOEmpresaResponse(model));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> getFilePasaje(@PathVariable UUID id) {
        byte[] pasajePdf = pasajeService.getOnePasajeDownload(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=pasaje.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(pasajePdf, headers, HttpStatus.OK);
    }

    @GetMapping("/from/{idPrecio}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_FUNCIONARIO', 'ROLE_EMPRESA_ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<List<PasajeDTOEmpresaResponse>> getPasajerosFromPrecio(@PathVariable UUID idPrecio) {
        var usuario = myUserComponent.getUser();
        var precio = precioService.findById(idPrecio);
        usuario.validIfIsAdminOrOwnerEmpresa(precio.getEmpresaId());
        return ResponseEntity.ok(pasajeService.getPasajesFromPrecio(idPrecio));
    }

    public ResponseEntity<Object> save(@RequestBody @Valid PasajesDTO dto, BindingResult bindingResult) {//Venta de pasajes al publico
        ValidationErrorsWithList validacao;
        validacao = ValidarCompraPasajes.validarPasajesDTO(bindingResult, dto, "/pasajes");
        if (!validacao.getErrorsList().isEmpty() || !validacao.getErrors().isEmpty())
            return ResponseEntity.unprocessableEntity().body(validacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(pasajeService.saveCliente(dto));
    }

    @PostMapping("/vender")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_FUNCIONARIO', 'ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> vender(@RequestBody @Valid PasajesDTOVenta dto, BindingResult bindingResult) {
        ValidationErrorsWithList validacao;
        var usuario = myUserComponent.getUser();
        var viaje = viajeService.findById(dto.idViaje());

        usuario.validIfIsMyEmpresa(viaje.getEmpresaId());
        empresaEnabled.validEmpresaEnabled(viaje.getEmpresaId());

        validacao = ValidarCompraPasajes.validarPasajesDTOVenta(bindingResult, dto, "/pasajes/vender");
        if (!validacao.getErrorsList().isEmpty() || !validacao.getErrors().isEmpty())
            return ResponseEntity.unprocessableEntity().body(validacao);

        var idPago = pasajeService.saveEmpresa(dto, viaje);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CodigoPago(idPago));
    }

    @DeleteMapping("{id}")//Habiliado solo para el rembolso fijo
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_FUNCIONARIO', 'ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Mensaje> rembolso(@PathVariable UUID idPasaje) {
        pasajeService.delete(idPasaje);
        return ResponseEntity.noContent().build();
    }
}
