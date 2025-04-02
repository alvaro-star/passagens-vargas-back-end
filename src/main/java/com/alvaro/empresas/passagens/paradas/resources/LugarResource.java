package com.alvaro.empresas.passagens.paradas.resources;

import com.alvaro.empresas.passagens.dtos.PageOutput;
import com.alvaro.empresas.passagens.paradas.dtos.LugarCreateDTO;
import com.alvaro.empresas.passagens.paradas.dtos.LugarUpdateDTO;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.services.LugarService;
import com.alvaro.empresas.passagens.paradas.services.ParadaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("lugares")
@SecurityRequirement(name = "bearer-key")
public class LugarResource {
    @Autowired
    private LugarService lugarService;
    @Autowired
    private ParadaService paradaService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageOutput<LugarModel> findAll(@PageableDefault(size = 10) Pageable pageable) {
        return lugarService.findAll(pageable);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public LugarModel findById(@PathVariable Integer id) {
        return lugarService.findById(id);
    }

    @GetMapping("{id}/paradas")
    @ResponseStatus(HttpStatus.OK)
    public PageOutput<ParadaModel> findParadasFromLugar(@PathVariable Integer id, Pageable pageable) {
        return paradaService.findByLugarId(id, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public LugarModel save(@RequestBody @Valid LugarCreateDTO dto) {
        return lugarService.save(dto);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public LugarModel update(@PathVariable Integer id, @RequestBody @Valid LugarUpdateDTO dto) {
        return lugarService.update(dto, id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void delete(@PathVariable Integer id) {
        lugarService.delete(id);
    }
}