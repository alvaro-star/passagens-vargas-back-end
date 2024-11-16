package com.alvaro.empresas.passagens.paradas.resources;

import com.alvaro.empresas.passagens.enums.TypeParada;
import com.alvaro.empresas.passagens.helpers.Mensaje;
import com.alvaro.empresas.passagens.helpers.beans.MyUserComponent;
import com.alvaro.empresas.passagens.helpers.validators.EmpresaEnabled;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOUpdate;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.services.ParadaService;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.services.ViajeEmpresaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/paradas")
@SecurityRequirement(name = "bearer-key")
public class ParadaResource {
    @Autowired
    private ParadaService paradaService;
    @Autowired
    private MyUserComponent myUserComponent;
    @Autowired
    private ViajeEmpresaService viajeEmpresaService;
    @Autowired
    private EmpresaEnabled empresaEnabled;


    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Page<ParadaDTO>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(paradaService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParadaDTOComplete> getOne(@PathVariable Integer id) {
        return ResponseEntity.ok(paradaService.getOne(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Object> save(@RequestBody @Valid ParadaDTO dto) {
        var viajeModel = this.viajeEmpresaService.findById(dto.idViaje());
        var user = myUserComponent.getUser();
        user.validIfIsMyEmpresa(viajeModel.getEmpresaId());

        if (viajeEmpresaService.hasPasajes(viajeModel.getPrecios()))
            return ResponseEntity.badRequest().body(new Mensaje("El viaje ya posee un pasaje registrado"));
        return ResponseEntity.status(HttpStatus.CREATED).body(paradaService.save(dto, viajeModel));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Object> update(@Valid @RequestBody ParadaDTOUpdate dto, @PathVariable Integer id) {
        var paradaModel = paradaService.findById(id);
        var userLogin = myUserComponent.getUser();
        userLogin.validIfIsMyEmpresa(paradaModel.getEmpresaId());

        if (viajeEmpresaService.hasPasajes(paradaModel.getViaje().getPrecios()))
            return ResponseEntity.badRequest().body(new Mensaje("El viaje ya posee un pasaje registrado"));
        return ResponseEntity.ok(paradaService.update(dto, paradaModel));
    }

    @DeleteMapping("/{id}")//Mejorar politica de exclusion, solo se puede eliminar si nádie pago o compro
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Mensaje> delete(@PathVariable Integer id) {
        var model = paradaService.findById(id);
        var userLogin = myUserComponent.getUser();

        if (!userLogin.hasRole(RoleList.ROLE_ADMIN.toString())) {
            userLogin.validIfIsMyEmpresa(model.getEmpresaId());
            empresaEnabled.validEmpresaEnabled(model.getEmpresaId());
        }

        if (!model.getViaje().getAutobus().isEnable())
            return ResponseEntity.badRequest().body(new Mensaje("El autobus esta inhabilitado"));
        int indice = -1;

        if (!model.getTipo().equals(TypeParada.CAMINO))
            return ResponseEntity.badRequest().body(new Mensaje("No se puede eliminar la salida o el destino"));

        ParadaModel aux;
        for (int i = 0; i < model.getViaje().getParadas().size(); i++) {
            aux = model.getViaje().getParadas().get(i);
            if (aux.getTipo().equals(TypeParada.DESTINO) && aux.getDataHora().isBefore(LocalDateTime.now()))
                return ResponseEntity.badRequest().body(new Mensaje("No se puede eliminar una parada de un viaje del pasado"));
            if (aux.getId().equals(model.getId()))
                indice = i;
        }
        if (indice == -1)
            return ResponseEntity.badRequest().body(new Mensaje("La parada no esta relacionado"));
        //Causa de nao exclusao: a o relacionamento com viaje
        if (viajeEmpresaService.hasPasajes(model.getViaje().getPrecios()))
            return ResponseEntity.badRequest().body(new Mensaje("El viaje ya esta relacionado con un pasaje"));

        model.getViaje().getParadas().remove(indice);

        paradaService.delete(model);
        return ResponseEntity.noContent().build();
    }
}
