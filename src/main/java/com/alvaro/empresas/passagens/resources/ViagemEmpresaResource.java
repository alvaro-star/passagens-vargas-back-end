package com.alvaro.empresas.passagens.resources;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.alvaro.empresas.passagens.dtos.PageOutput;
import com.alvaro.empresas.passagens.dtos.viagens.ViagemDTOUpdate;
import com.alvaro.empresas.passagens.dtos.viagens.busca.ViagemDTOSolicitacaoEmpresa;
import com.alvaro.empresas.passagens.dtos.viagens.empresa.ViagemDTOCreate;
import com.alvaro.empresas.passagens.dtos.viagens.empresa.ViagemDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.viagens.empresa.ViagemDTOFormCopy;
import com.alvaro.empresas.passagens.dtos.viagens.empresa.ViagemDTOListBuscaEmpresa;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaPassagemModel;
import com.alvaro.empresas.passagens.pagamentos.services.FaturaPassagemService;
import com.alvaro.empresas.passagens.services.ViagemEmpresaService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("empresa/viagens")
@SecurityRequirement(name = "bearer-key")
public class ViagemEmpresaResource {
    @Autowired
    private ViagemEmpresaService viagemEmpresaService;
    @Autowired
    private FaturaPassagemService faturaPassagemService;

    @GetMapping("{id}/pdf")
    public ResponseEntity<Object> getPdfFromViagem(@PathVariable("id") UUID idViagem) {
        byte[] viajeRelatorio = viagemEmpresaService.getPdfFromViagem(idViagem);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=viaje.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(viajeRelatorio, headers, HttpStatus.OK);
    }

    @GetMapping("{idViagem}/pagamentos")
    @ResponseStatus(HttpStatus.OK)
    public PageOutput<FaturaPassagemModel> findAll(@PathVariable UUID idViagem,
                                                   @PageableDefault(sort = "created_at", direction = Sort.Direction.DESC) Pageable pageable) {
        return faturaPassagemService.findAllFromViagem(idViagem, pageable);
    }

    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public List<ViagemDTOListBuscaEmpresa> getViagensByData(@PathVariable(value = "idEmpresa") UUID idEmpresa,
                                                            @RequestBody @Valid ViagemDTOSolicitacaoEmpresa dto) {
        return viagemEmpresaService.findViagensByDay(idEmpresa, dto);
    }

    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ViagemDTOEmpresaResponse save(@Valid @RequestBody ViagemDTOCreate dto) {
        return viagemEmpresaService.save(dto);
    }

    @PostMapping("duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public void saveViagensCopyFromDay(@RequestBody @Valid ViagemDTOFormCopy dto) {
        viagemEmpresaService.saveOneCopy(dto);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ViagemDTOUpdate update(@PathVariable UUID id, @RequestBody @Valid ViagemDTOUpdate dto) {
        return viagemEmpresaService.update(id, dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public void delete(@PathVariable UUID id) {
        viagemEmpresaService.delete(id);
    }
}