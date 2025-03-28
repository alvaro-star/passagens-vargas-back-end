package com.alvaro.empresas.passagens.paradas.resources;

import com.alvaro.empresas.passagens.paradas.dtos.LugarDTO;
import com.alvaro.empresas.passagens.paradas.dtos.LugarDTOUpdate;
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
@RequestMapping("/lugares")
@SecurityRequirement(name = "bearer-key")
public class LugarResource {
    @Autowired
    private LugarService lugarService;
    @Autowired
    private CidadeService cidadeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<LugarDTO> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return lugarService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LugarDTO getOne(@PathVariable(value = "id") Integer id) {
        var model = lugarService.findById(id);
        return new LugarDTO(model);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public LugarDTO save(@RequestBody @Valid LugarDTO dto) {
        var cidade = cidadeService.findById(dto.idCidade());
        var model = lugarService.save(dto, cidade);
        return new LugarDTO(model);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public LugarDTO update(@PathVariable(value = "id") Integer id, @RequestBody @Valid LugarDTOUpdate dto) {
        var model = lugarService.update(dto, id);
        return new LugarDTO(model);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void delete(@PathVariable(value = "id") Integer id) {
        var model = lugarService.findById(id);
        lugarService.delete(model);
    }
}