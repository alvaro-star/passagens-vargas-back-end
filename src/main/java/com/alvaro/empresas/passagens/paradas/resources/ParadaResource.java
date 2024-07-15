package com.alvaro.empresas.passagens.paradas.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.enums.EnumParada;
import com.alvaro.empresas.passagens.helpers.beans.MyUserService;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOUpdate;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.services.ParadaService;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.services.EmpresaService;
import com.alvaro.empresas.passagens.services.ViajeService;
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
    private final ParadaService paradaService;
    private final MyUserService myUserService;
    private final ViajeService viajeService;
    private final EmpresaService empresaService;

    @Autowired
    public ParadaResource(
            ParadaService paradaService,
            MyUserService myUserService,
            ViajeService viajeService,
            EmpresaService empresaService
    ) {
        this.paradaService = paradaService;
        this.myUserService = myUserService;
        this.viajeService = viajeService;
        this.empresaService = empresaService;
    }


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
        var viajeModel = this.viajeService.findById(dto.idViaje());
        var userLogin = myUserService.getUser();

        if (!userLogin.isMyEmpresa(viajeModel.getEmpresa().getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Mensaje("El usuario no esta relacionado con este Viaje"));
        }
        var empresa = viajeModel.getEmpresa();
        if (empresa.getBloqued() || !empresa.getEnabled())
            return ResponseEntity.badRequest().body(new Mensaje("La empresa esta bloqueada"));

        return ResponseEntity.status(HttpStatus.CREATED).body(paradaService.save(dto, viajeModel));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Object> update(@Valid @RequestBody ParadaDTOUpdate dto, @PathVariable Integer id) {
        var paradaModel = paradaService.findById(id);
        var userLogin = myUserService.getUser();
        if (!userLogin.isMyEmpresa(paradaModel.getEmpresa().getId()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Mensaje("El usuario no esta relacionado con esta Parada"));
        var empresa = paradaModel.getEmpresa();
        if (empresa.getBloqued() || !empresa.getEnabled())
            return ResponseEntity.badRequest().body(new Mensaje("La empresa esta bloqueada"));
        return ResponseEntity.ok(paradaService.update(dto, paradaModel));
    }

    @DeleteMapping("/{id}")//Mejorar politica de exclusion, solo se puede eliminar si nádie pago o compro
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Mensaje> delete(@PathVariable Integer id) {
        var model = paradaService.findById(id);
        var userLogin = myUserService.getUser();
        if (!(userLogin.hasRole(RoleList.ROLE_ADMIN.toString()) || userLogin.isMyEmpresa(model.getEmpresa().getId())))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Mensaje("El usuario no esta relacionado con esta Parada"));

        int indice = -1;

        if (!model.getTipo().equals(EnumParada.CAMINO))
            return ResponseEntity.badRequest().body(new Mensaje("No se puede eliminar la salida o el destino"));

        ParadaModel aux;
        for (int i = 0; i < model.getViaje().getParadas().size(); i++) {
            aux = model.getViaje().getParadas().get(i);
            if (aux.getTipo().equals(EnumParada.DESTINO) && aux.getDataHora().isBefore(LocalDateTime.now()))
                return ResponseEntity.badRequest().body(new Mensaje("No se puede eliminar una parada de un viaje del pasado"));
            if (aux.getId().equals(model.getId()))
                indice = i;
        }
        if (indice == -1)
            return ResponseEntity.badRequest().body(new Mensaje("La parada no esta relacionado"));
        //Causa de nao exclusao: a o relacionamento com viaje
        if (viajeService.hasPasajes(model.getViaje().getPrecios()))
            return ResponseEntity.badRequest().body(new Mensaje("El viaje ya esta relacionado con un pasaje"));

        model.getViaje().getParadas().remove(indice);

        paradaService.delete(model);
        return ResponseEntity.noContent().build();
    }
}
