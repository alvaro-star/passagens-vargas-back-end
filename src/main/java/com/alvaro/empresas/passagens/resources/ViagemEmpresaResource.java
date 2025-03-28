package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.viagens.busca.ViagemDTOSolicitacaoFromOnibus;
import com.alvaro.empresas.passagens.onibus.services.OnibusService;
import com.alvaro.empresas.passagens.dtos.viagens.busca.ViagemDTOSolicitacaoEmpresa;
import com.alvaro.empresas.passagens.dtos.viagens.busca.ViagemDTOSolicitacaoFromEmpresa;
import com.alvaro.empresas.passagens.dtos.viagens.empresa.ViagemDTOCreate;
import com.alvaro.empresas.passagens.dtos.viagens.empresa.ViagemDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.viagens.empresa.ViagemDTOFormCopy;
import com.alvaro.empresas.passagens.dtos.viagens.empresa.ViagemDTOListBuscaEmpresa;
import com.alvaro.empresas.passagens.dtos.viagens.ViagemDTOUpdate;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.services.ViagemEmpresaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/empresa/viagens")
@SecurityRequirement(name = "bearer-key")
public class ViagemEmpresaResource {
    @Autowired
    private ViagemEmpresaService viagemEmpresaService;
    @Autowired
    private UserLoguedComponent userLogued;
    @Autowired
    private OnibusService onibusService;

    @GetMapping("{id}/pdf")
    public ResponseEntity<Object> getPdfFromViagem(@PathVariable("id") UUID idViagem) {
        byte[] viajeRelatorio = viagemEmpresaService.getPdfFromViagem(idViagem);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=viaje.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(viajeRelatorio, headers, HttpStatus.OK);
    }

    @PostMapping("/from/empresa")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public Page<ViagemDTOListBuscaEmpresa> getAllFromEmpresaBetweenMonth(
            @RequestBody @Valid ViagemDTOSolicitacaoFromEmpresa solicitacao,
            @PageableDefault(sort = "dataHoraSaida") Pageable pageable) {
        userLogued.validIfIsAdminOrOwnerEmpresa(solicitacao.idEmpresa());
        return viagemEmpresaService.findAllByEmpresaBetweenDates(solicitacao, pageable);
    }

    @PostMapping("/from/onibus")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public Page<ViagemDTOListBuscaEmpresa> getAllFromOnibus(
            @RequestBody @Valid ViagemDTOSolicitacaoFromOnibus solicitacao,
            @PageableDefault(sort = "dataHoraSaida") Pageable pageable) {
        var onibusModel = onibusService.findById(solicitacao.idOnibus());
        userLogued.validIfIsAdminOrOwnerEmpresa(onibusModel.getEmpresaId());
        return viagemEmpresaService.findAllFromOnibus(onibusModel, solicitacao, pageable);
    }

    @PostMapping("/{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public List<ViagemDTOListBuscaEmpresa> getViagemFromDia(@PathVariable(value = "idEmpresa") UUID idEmpresa,
                                                            @RequestBody @Valid ViagemDTOSolicitacaoEmpresa dto) {
        userLogued.validIfIsAdminOrOwnerEmpresa(idEmpresa);
        if (dto.idCidadeDestino() == null || dto.idCidadeDestino() == 0)
            return viagemEmpresaService.findViagensBySaida(idEmpresa, dto);
        else
            return viagemEmpresaService.findViagensByDay(idEmpresa, dto);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ViagemDTOEmpresaResponse save(@Valid @RequestBody ViagemDTOCreate dto) {
        var onibus = onibusService.findById(dto.idOnibus());
        userLogued.validIfIsMyEmpresa(onibus.getEmpresaId());
        return viagemEmpresaService.save(dto, onibus);
    }

    @PostMapping("/create/copy")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public void saveViagensCopyFromDay(@RequestBody @Valid ViagemDTOFormCopy dto) {
        var viagem = viagemEmpresaService.findById(dto.idViagem());
        userLogued.validIfIsMyEmpresa(viagem.getEmpresaId());
        viagemEmpresaService.saveOneCopy(dto, viagem);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ViagemDTOUpdate update(@PathVariable UUID id, @RequestBody @Valid ViagemDTOUpdate dto) {
        var viagemModel = viagemEmpresaService.findById(id);
        userLogued.validIfIsMyEmpresa(viagemModel.getEmpresaId());
        return viagemEmpresaService.update(dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public void delete(@PathVariable UUID id) {
        var model = viagemEmpresaService.findById(id);
        userLogued.validIfIsMyEmpresa(model.getEmpresaId());
        viagemEmpresaService.delete(model);
    }
}