package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.viagens.buyer.ViagemResponseDTO;
import com.alvaro.empresas.passagens.dtos.viagens.ViagemSolicitacaoDTO;
import com.alvaro.empresas.passagens.helpers.validations.groups.IClientCommonUser;
import com.alvaro.empresas.passagens.services.ViagemService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("viagens")
@SecurityRequirement(name = "bearer-key")
public class ViagemResource {
    @Autowired
    private ViagemService viagemService;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ViagemResponseDTO findById(@PathVariable UUID id) {
        return viagemService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ViagemResponseDTO> getViagemFromDia(@Validated(IClientCommonUser.class) ViagemSolicitacaoDTO dto) {
        return viagemService.getViagensFromDia(dto);
    }
}