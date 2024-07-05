package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajeDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTO;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTOVenta;
import com.alvaro.empresas.passagens.enums.MetodoPagamentoEnum;
import com.alvaro.empresas.passagens.helpers.beans.MyUserService;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pasajes")
@SecurityRequirement(name = "bearer-key")
public class PasajeResource {
    private final PasajeService pasajeService;
    private final MyUserService myUserService;
    private final ViajeService viajeService;
    private final PrecioService precioService;

    @Autowired
    public PasajeResource(PasajeService pasajeService, MyUserService myUserService, ViajeService viajeService, PrecioService precioService) {
        this.pasajeService = pasajeService;
        this.myUserService = myUserService;
        this.viajeService = viajeService;
        this.precioService = precioService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_CLIENTE', 'ROLE_ADMIN')")
    public ResponseEntity<PasajeDTOEmpresaResponse> getOne(@PathVariable(value = "id") UUID id) {
        var model = pasajeService.findById(id);
        var salida = new ParadaDTOComplete(model.getSalida(), null);
        var destino = new ParadaDTOComplete(model.getDestino(), null);
        return ResponseEntity.ok(new PasajeDTOEmpresaResponse(model, salida, destino));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> getFilePasaje(@PathVariable(value = "id") UUID id) {
        var model = pasajeService.findById(id);
        byte[] pasajePdf = pasajeService.getOnePasajeDownload(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pasaje.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(pasajePdf, headers, HttpStatus.OK);
    }

    @GetMapping("/from/{idPrecio}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_FUNCIONARIO', 'ROLE_EMPRESA_ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<List<PasajeDTOEmpresaResponse>> getPasajerosFromPrecio(@PathVariable(value = "idPrecio") UUID idPrecio) {
        var usuario = myUserService.getUser();
        var precio = precioService.findById(idPrecio);
        if (usuario.hasRole("ROLE_ADMIN") || usuario.isMyEmpresa(precio.getEmpresa().getId()))
            return ResponseEntity.ok(pasajeService.getPasajesFromPrecio(idPrecio));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
    }

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody PasajesDTO dto, BindingResult bindingResult) {
        ValidationErrorsWithList validacao;
        validacao = ValidarCompraPasajes.validarPasajesDTO(bindingResult, dto, "/pasajes");
        if (!validacao.getErrorsList().isEmpty() || !validacao.getErrors().isEmpty())
            return ResponseEntity.unprocessableEntity().body(validacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(pasajeService.save(dto, MetodoPagamentoEnum.QR, true, true));
    }

    @PostMapping("/vender")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_FUNCIONARIO', 'ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<?> vender(@RequestBody @Valid PasajesDTOVenta dto, BindingResult bindingResult) {
        ValidationErrorsWithList validacao;
        var usuario = myUserService.getUser();
        var viaje = viajeService.findById(dto.idViaje());
        if (!usuario.isMyEmpresa(viaje.getEmpresa().getId()))
            return ResponseEntity.unprocessableEntity().body(new Mensaje("No se puede vender el pasaje de otra empresa"));
        validacao = ValidarCompraPasajes.validarPasajesDTOVenta(bindingResult, dto, "/pasajes/vender");
        if (!validacao.getErrorsList().isEmpty() || !validacao.getErrors().isEmpty())
            return ResponseEntity.unprocessableEntity().body(validacao);

        byte[] pasajesPDF = pasajeService.saveEmpresa(viaje.getEmpresa().getNombre(), dto, dto.metodo(), viaje, false, false);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pasajes.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

        return new ResponseEntity<>(pasajesPDF, headers, HttpStatus.OK);
    }

}
