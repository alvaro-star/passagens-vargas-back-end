package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.pasagens.CodigoPago;
import com.alvaro.empresas.passagens.dtos.pasagens.PassagemDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.pasagens.PassagensDTO;
import com.alvaro.empresas.passagens.dtos.pasagens.PassagensDTOVenta;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.helpers.validators.ValidEnabledEntities;
import com.alvaro.empresas.passagens.services.PassagemService;
import com.alvaro.empresas.passagens.services.PrecoService;
import com.alvaro.empresas.passagens.services.ViagemService;
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
@RequestMapping("/passagens")
@SecurityRequirement(name = "bearer-key")
public class PasagensResource {
    @Autowired
    private PassagemService passagemService;
    @Autowired
    private UserLoguedComponent userLogued;
    @Autowired
    private ViagemService viagemService;
    @Autowired
    private PrecoService precoService;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public PassagemDTOEmpresaResponse findById(@PathVariable UUID id) {
        return passagemService.findById(id);
    }

    @GetMapping("{id}/download")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getFilePasaje(@PathVariable UUID id) {
        byte[] pasajePdf = passagemService.obterDownloadPassagem(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=pasaje.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(pasajePdf, headers, HttpStatus.OK);
    }

    @GetMapping("/from/{idPreco}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_FUNCIONARIO', 'ROLE_EMPRESA_ADMIN', 'ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public List<PassagemDTOEmpresaResponse> getPasajerosFromPreco(@PathVariable UUID idPreco) {
        var precio = precoService.findById(idPreco);
        userLogued.validIfIsAdminOrOwnerEmpresa(precio.getEmpresaId());
        return passagemService.getPassagensByPreco(idPreco);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Object save(@RequestBody @Valid PassagensDTO dto, BindingResult bindingResult) { // Venta de pasajes al
                                                                                            // público
        return passagemService.saveCliente(dto, bindingResult);
    }

    @PostMapping("/vender")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_FUNCIONARIO', 'ROLE_EMPRESA_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CodigoPago vender(@RequestBody @Valid PassagensDTOVenta dto, BindingResult bindingResult) {
        var viaje = viagemService.findById(dto.idViagem());
        userLogued.validIfIsMyEmpresa(viaje.getEmpresaId());
        ValidEnabledEntities.validEmpresa(viaje.getEmpresa());

        var idPago = passagemService.saveEmpresa(dto, viaje, bindingResult);
        return new CodigoPago(idPago);
    }

    @DeleteMapping("{id}") // Habilitado solo para el reembolso fijo
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_FUNCIONARIO', 'ROLE_EMPRESA_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rembolso(@PathVariable UUID id) {
        passagemService.delete(id);
    }
}
