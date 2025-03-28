package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.viagens.busca.ViagemDTOListBusca;
import com.alvaro.empresas.passagens.dtos.viagens.busca.ViagemDTOSolicitacao;
import com.alvaro.empresas.passagens.dtos.viagens.ViagemDTOResponse;
import com.alvaro.empresas.passagens.services.ViagemService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/viagens")
@SecurityRequirement(name = "bearer-key")
public class ViagemResource {
    @Autowired
    private ViagemService viagemService;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ViagemDTOResponse getOne(@PathVariable UUID id) {
        return viagemService.getOne(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ViagemDTOListBusca> getViagemFromDia(@RequestBody @Valid ViagemDTOSolicitacao dto) {
        return viagemService.getViagensFromDia(dto);
    }
}