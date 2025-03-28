package com.alvaro.empresas.passagens.paradas.resources;

import com.alvaro.empresas.passagens.helpers.Mensagem;
import com.alvaro.empresas.passagens.paradas.dtos.CidadeDTO;
import com.alvaro.empresas.passagens.paradas.dtos.DepartamentoDTO;
import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import com.alvaro.empresas.passagens.paradas.services.DepartamentoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/departamentos")
@SecurityRequirement(name = "bearer-key")
public class DepartamentoResource {
    @Autowired
    private DepartamentoService departamentoService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<DepartamentoDTO> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return departamentoService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DepartamentoDTO getOne(@PathVariable(value = "id") Integer id) {
        DepartamentoModel model = departamentoService.findById(id);
        List<CidadeDTO> cidades = new ArrayList<>();
        model.getCidades().forEach(cidadeModel -> cidades.add(new CidadeDTO(cidadeModel)));
        return new DepartamentoDTO(model, cidades);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public DepartamentoDTO save(@RequestBody @Valid DepartamentoDTO dto) {
        DepartamentoModel model = departamentoService.save(dto);
        return new DepartamentoDTO(model);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public DepartamentoDTO update(@PathVariable(value = "id") Integer id, @RequestBody @Valid DepartamentoDTO dto) {
        DepartamentoModel model = departamentoService.update(dto, id);
        return new DepartamentoDTO(model);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void delete(@PathVariable(value = "id") Integer id) {
        departamentoService.delete(id);
    }
}