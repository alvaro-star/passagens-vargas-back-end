package com.alvaro.empresas.passagens.paradas.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
import com.alvaro.empresas.passagens.paradas.dtos.ParadaCreateDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaResponseDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaUpdateDTO;
import com.alvaro.empresas.passagens.paradas.services.ParadaService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/paradas")
@SecurityRequirement(name = "bearer-key")
public class ParadaResource {
    @Autowired
    private ParadaService paradaService;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ParadaResponseDTO findById(@PathVariable Integer id) {
        return paradaService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public PageOutput<ParadaResponseDTO> findAll(@PageableDefault(size = 10) Pageable pageable) {
        return paradaService.findAll(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ParadaResponseDTO save(@RequestBody @Valid ParadaCreateDTO dto) {
        return paradaService.save(dto);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ParadaResponseDTO update(@PathVariable Integer id, @Valid @RequestBody ParadaUpdateDTO dto) {
        return paradaService.update(id, dto);
    }

    @DeleteMapping("{id}") // Melhorar política de exclusão, só pode excluir se ninguém pagou ou comprou
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public void delete(@PathVariable Integer id) {
        paradaService.delete(id);
    }
}