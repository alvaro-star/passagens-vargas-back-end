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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.alvaro.empresas.passagens.dtos.PageOutput;
import com.alvaro.empresas.passagens.paradas.dtos.CidadeCreateDTO;
import com.alvaro.empresas.passagens.paradas.dtos.CidadeUpdateDTO;
import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.services.CidadeService;
import com.alvaro.empresas.passagens.paradas.services.LugarService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("cidades")
@SecurityRequirement(name = "bearer-key")
public class CidadeResource {
    @Autowired
    private CidadeService cidadeService;
    @Autowired
    private LugarService lugarService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageOutput<CidadeModel> findAll(
            @RequestParam(value = "nome", defaultValue = "", required = false) String nome,
            @PageableDefault(size = 20) Pageable pageable) {
        return cidadeService.findAll(nome, pageable);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public CidadeModel findById(@PathVariable Integer id) {
        return cidadeService.findById(id);
    }

    @GetMapping("{id}/lugares")
    @ResponseStatus(HttpStatus.OK)
    public PageOutput<LugarModel> findLugares(@PathVariable Integer id, Pageable pageable) {
        return lugarService.findByCidadeId(id, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public CidadeModel save(@Valid @RequestBody CidadeCreateDTO dto) {
        return cidadeService.save(dto);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public CidadeModel update(@Valid @RequestBody CidadeUpdateDTO dto, @PathVariable Integer id) {
        return cidadeService.update(dto, id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void delete(@PathVariable Integer id) {
        cidadeService.delete(id);
    }
}