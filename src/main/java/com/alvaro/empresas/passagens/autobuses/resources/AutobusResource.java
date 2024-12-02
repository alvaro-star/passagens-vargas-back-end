package com.alvaro.empresas.passagens.autobuses.resources;

import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTOResponse;
import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.services.AutobusService;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/autobuses")
@SecurityRequirement(name = "bearer-key")
public class AutobusResource {
    @Autowired
    private AutobusService autobusService;
    @Autowired
    private UserLoguedComponent userLogued;

    @GetMapping("/from/{idEmpresa}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Page<AutobusDTOResponse>> getAutobusesFromEmpresa(@PathVariable UUID idEmpresa, @PageableDefault(size = 10, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable) {
        userLogued.validIfIsAdminOrOwnerEmpresa(idEmpresa);
        return ResponseEntity.ok().body(autobusService.findAllFromEmpresa(idEmpresa, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutobusDTOResponse> getOne(@PathVariable Integer id) {
        return ResponseEntity.ok().body(autobusService.getOne(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> save(@RequestBody @Valid AutobusDTO dto, BindingResult bindingResult) {
        userLogued.validIfIsMyEmpresa(dto.idEmpresa());
        return ResponseEntity.status(HttpStatus.CREATED).body(autobusService.salvar(dto, bindingResult));
    }

    //Solo el administrador
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody @Valid AutobusDTOUpdate dto, BindingResult bindingResult) {

        var autobus = autobusService.findById(id);
        userLogued.validIfIsMyEmpresa(autobus.getEmpresaId());
        return ResponseEntity.ok().body(autobusService.update(dto, autobus, bindingResult));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        var model = autobusService.findById(id);

        userLogued.validIfIsMyEmpresa(model.getEmpresaId());
        autobusService.delete(model);
        return ResponseEntity.noContent().build();
    }
}
