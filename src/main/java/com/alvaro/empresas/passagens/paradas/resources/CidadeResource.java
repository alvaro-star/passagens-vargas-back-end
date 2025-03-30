package com.alvaro.empresas.passagens.paradas.resources;

import com.alvaro.empresas.passagens.dtos.PageOutput;
import com.alvaro.empresas.passagens.paradas.dtos.CidadeCreateDTO;
import com.alvaro.empresas.passagens.paradas.dtos.CidadeUpdateDTO;
import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.services.CidadeService;
import com.alvaro.empresas.passagens.paradas.services.LugarService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cidades")
@SecurityRequirement(name = "bearer-key")
public class CidadeResource {
    @Autowired
    private CidadeService cidadeService;
    @Autowired
    private LugarService lugarService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<CidadeModel> findAll(@PageableDefault(size = 20) Pageable pageable) {
        return cidadeService.findAll(pageable);
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

    @GetMapping("/{nome}/like")
    @ResponseStatus(HttpStatus.OK)
    public Page<CidadeModel> findAllLike(@PathVariable(value = "nome") String nome, @PageableDefault(size = 8, sort = "nome") Pageable pageable) {
        return cidadeService.findByNomeContaining(nome.toUpperCase(), pageable);
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