package com.alvaro.empresas.passagens.paradas.resources;

import java.util.ArrayList;
import java.util.List;

import com.alvaro.empresas.passagens.paradas.dtos.DepartamentoOutputDTO;
import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import com.alvaro.empresas.passagens.paradas.services.CidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import com.alvaro.empresas.passagens.paradas.dtos.CidadeDTO;
import com.alvaro.empresas.passagens.paradas.dtos.DepartamentoInputDTO;
import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import com.alvaro.empresas.passagens.paradas.services.DepartamentoService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/departamentos")
@SecurityRequirement(name = "bearer-key")
public class DepartamentoResource {
    @Autowired
    private DepartamentoService departamentoService;
    @Autowired
    private CidadeService cidadeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<DepartamentoOutputDTO> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return departamentoService.findAll(pageable);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public DepartamentoModel findById(@PathVariable Integer id) {
        return departamentoService.findById(id);
    }




    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public DepartamentoModel save(@RequestBody @Valid DepartamentoInputDTO dto) {
        return departamentoService.save(dto);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void update(@PathVariable Integer id, @RequestBody @Valid DepartamentoInputDTO dto) {
        departamentoService.update(dto, id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void delete(@PathVariable Integer id) {
        departamentoService.delete(id);
    }
}