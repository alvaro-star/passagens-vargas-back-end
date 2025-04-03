package com.alvaro.empresas.passagens.resources;

import java.util.UUID;

import com.alvaro.empresas.passagens.dtos.viagens.seller.ViagemResponseDTO;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.alvaro.empresas.passagens.dtos.EmpresaInputDTO;
import com.alvaro.empresas.passagens.dtos.PageOutput;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.onibus.dtos.OnibusDTOResponse;
import com.alvaro.empresas.passagens.onibus.services.OnibusService;
import com.alvaro.empresas.passagens.pagamentos.services.relatorios.RelatorioService;
import com.alvaro.empresas.passagens.security.dtos.RegisterDTOEmpresaAdmin;
import com.alvaro.empresas.passagens.services.EmpresaService;
import com.alvaro.empresas.passagens.services.ViagemEmpresaService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("empresas")
@SecurityRequirement(name = "bearer-key")
public class EmpresaResource {
    @Autowired
    private EmpresaService empresaService;
    @Autowired
    private OnibusService onibusService;
    @Autowired
    private ViagemEmpresaService viagemEmpresaService;
    @Autowired
    private RelatorioService relatorioService;

    @PostMapping("admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void registerEmpresaAdmin(@RequestBody @Valid RegisterDTOEmpresaAdmin empresaAdmin) {
        empresaService.saveAdmin(empresaAdmin);
    }

    @DeleteMapping("admin/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerEmpresario(@PathVariable String email) {
        empresaService.removerAdmin(email);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public PageOutput<EmpresaModel> findAll(@PageableDefault Pageable pageable) {
        return empresaService.findAll(pageable);
    }


    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmpresaModel findById(@PathVariable UUID id) {
        return empresaService.findById(id);
    }

    @GetMapping("{id}/onibus")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public PageOutput<OnibusDTOResponse> findOnibusFromEmpresa(
            @PathVariable UUID id,
            @PageableDefault(sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return onibusService.findByEmpresaId(id, pageable);
    }

    @GetMapping("{id}/viagens")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public PageOutput<ViagemResponseDTO> findAllFromEmpresaBetweenMonth(
            @PathVariable UUID id,
            @RequestParam String mesAnalise,
            @PageableDefault(sort = "dataHoraSaida") Pageable pageable) {
        return viagemEmpresaService.findAllByEmpresaBetweenDates(id, mesAnalise, pageable);
    }

    @GetMapping("{id}/relatorio")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<byte[]> getRelatorioByEmpresa(@PathVariable UUID id, @RequestParam String mesAnalise) {
        byte[] relatorioPDF = relatorioService.makeRelatorioMensal(id, mesAnalise);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=relatorio.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(relatorioPDF, headers, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public EmpresaModel save(@RequestBody @Valid EmpresaInputDTO dto) {
        return empresaService.save(dto);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable UUID id, @RequestBody @Valid EmpresaInputDTO dto) {
        empresaService.update(dto, id);
    }

    @DeleteMapping("{id}/bloquedCount")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disabled(@PathVariable UUID id) {
        empresaService.bloquedCount(id);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        empresaService.delete(id);
    }
}