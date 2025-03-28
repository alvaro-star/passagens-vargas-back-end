package com.alvaro.empresas.passagens.paradas.resources;


import com.alvaro.empresas.passagens.enums.TipoParada;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.helpers.validators.ValidEnabledEntities;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOUpdate;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.services.ParadaService;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.services.ViagemEmpresaService;
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

import java.time.LocalDateTime;

@RestController
@RequestMapping("/paradas")
@SecurityRequirement(name = "bearer-key")
public class ParadaResource {
    @Autowired
    private ParadaService paradaService;
    @Autowired
    private UserLoguedComponent userLogued;
    @Autowired
    private ViagemEmpresaService viagemEmpresaService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public Page<ParadaDTO> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return paradaService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ParadaDTOComplete getOne(@PathVariable Integer id) {
        return paradaService.getOne(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ParadaDTOComplete save(@RequestBody @Valid ParadaDTO dto) {
        var viagemModel = this.viagemEmpresaService.findById(dto.idViagem());
        userLogued.validIfIsMyEmpresa(viagemModel.getEmpresaId());

        if (viagemEmpresaService.hasPasajes(viagemModel.getPrecos()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A viagem já possui uma passagem registrada");
        return paradaService.save(dto, viagemModel);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ParadaDTOComplete update(@Valid @RequestBody ParadaDTOUpdate dto, @PathVariable Integer id) {
        var paradaModel = paradaService.findById(id);
        userLogued.validIfIsMyEmpresa(paradaModel.getEmpresaId());

        if (viagemEmpresaService.hasPasajes(paradaModel.getViagem().getPrecos()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A viagem já possui uma passagem registrada");
        return paradaService.update(dto, paradaModel);
    }

    @DeleteMapping("/{id}")//Melhorar política de exclusão, só pode excluir se ninguém pagou ou comprou
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public void delete(@PathVariable Integer id) {
        var model = paradaService.findById(id);

        if (!userLogued.hasRole(RoleList.ROLE_ADMIN)) {
            userLogued.validIfIsMyEmpresa(model.getEmpresaId());
            ValidEnabledEntities.validEmpresa(model.getEmpresa());
        }

        if (!model.getViagem().getOnibus().isHabilitado())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ônibus está desabilitado");
        int indice = -1;

        if (!model.getTipo().equals(TipoParada.CAMINHO))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível excluir a saída ou o destino");

        ParadaModel aux;
        for (int i = 0; i < model.getViagem().getParadas().size(); i++) {
            aux = model.getViagem().getParadas().get(i);
            if (aux.getTipo().equals(TipoParada.DESTINO) && aux.getDataHora().isBefore(LocalDateTime.now()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível excluir uma parada de uma viagem do passado");
            if (aux.getId().equals(model.getId()))
                indice = i;
        }
        if (indice == -1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A parada não está relacionada");
        //Causa de não exclusão: o relacionamento com viagem
        if (viagemEmpresaService.hasPasajes(model.getViagem().getPrecos()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A viagem já está relacionada com uma passagem");

        model.getViagem().getParadas().remove(indice);

        paradaService.delete(model);
    }
}