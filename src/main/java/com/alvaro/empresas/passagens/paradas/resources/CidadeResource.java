package com.alvaro.empresas.passagens.paradas.resources;

import com.alvaro.empresas.passagens.paradas.dtos.CidadeDTO;
import com.alvaro.empresas.passagens.paradas.dtos.CidadeDTOUpdate;
import com.alvaro.empresas.passagens.paradas.dtos.LugarDTO;
import com.alvaro.empresas.passagens.paradas.services.CidadeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cidades")
@SecurityRequirement(name = "bearer-key")
public class CidadeResource {
    @Autowired
    private CidadeService cidadeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<CidadeDTO> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return cidadeService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CidadeDTO getOne(@PathVariable Integer id) {
        return cidadeService.getOne(id);
    }

    @GetMapping("/{id}/lugares")
    @ResponseStatus(HttpStatus.OK)
    public List<LugarDTO> getLugaresFromCidade(@PathVariable Integer id) {
        var cidadeModel = cidadeService.findById(id);
        List<LugarDTO> lugares = new ArrayList<>();
        cidadeModel.getLugares().forEach(lugarModel -> lugares.add(new LugarDTO(lugarModel)));
        return lugares;
    }

    @GetMapping("/{nome}/like")
    @ResponseStatus(HttpStatus.OK)
    public Page<CidadeDTO> getAllLike(@PathVariable(value = "nome") String nome, @PageableDefault(size = 8, sort = "nome") Pageable pageable) {
        return cidadeService.findByNomeContaining(nome.toUpperCase(), pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public CidadeDTO save(@Valid @RequestBody CidadeDTO dto) {
        return cidadeService.save(dto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public CidadeDTO update(@Valid @RequestBody CidadeDTOUpdate dto, @PathVariable Integer id) {
        return cidadeService.update(dto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void delete(@PathVariable Integer id) {
        cidadeService.delete(id);
    }
}