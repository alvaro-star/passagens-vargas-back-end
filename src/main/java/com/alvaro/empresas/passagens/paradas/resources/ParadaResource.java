package com.alvaro.empresas.passagens.paradas.resources;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.helpers.beans.MyUserService;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOUpdate;
import com.alvaro.empresas.passagens.paradas.services.ParadaService;
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

@RestController
@RequestMapping("/paradas")
@SecurityRequirement(name = "bearer-key")
public class ParadaResource {
    private final ParadaService paradaService;

    private final MyUserService myUserService;
    private final ViajeService viajeService;

    @Autowired
    public ParadaResource(ParadaService paradaService, MyUserService myUserService, ViajeService viajeService) {
        this.paradaService = paradaService;
        this.myUserService = myUserService;
        this.viajeService = viajeService;
    }

    @GetMapping
    public ResponseEntity<Page<ParadaDTO>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(paradaService.getAll(pageable));
    }

    //Bloquear o acesso a outros tipos de usuarios
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Object> save(@RequestBody @Valid ParadaDTO dto) {
        var viajeModel = this.viajeService.findById(dto.idViaje());
        var userLogin = myUserService.getUser();
        if (!(userLogin.hasRole("ROLE_ADMIN") || userLogin.isMyEmpresa(viajeModel.getEmpresa().getId())))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Mensaje("El usuario no esta relacionado a un empresa"));
        return ResponseEntity.status(HttpStatus.CREATED).body(paradaService.save(dto, viajeModel));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParadaDTOComplete> getOne(@PathVariable Integer id) {
        return ResponseEntity.ok(paradaService.getOne(id));
    }

    @PutMapping("/{id}")
    //Validar
    public ResponseEntity<ParadaDTOComplete> update(@Valid @RequestBody ParadaDTOUpdate dto, @PathVariable Integer id) {
        return ResponseEntity.ok(paradaService.update(dto, id));
    }

    @DeleteMapping("/{id}")//Mejorar politica de exclusion, solo se puede eliminar si nádie pago o compro
    public ResponseEntity<Mensaje> delete(@PathVariable Integer id) {
        var model = paradaService.findById(id);
        var pagos = model.getViaje().getPagos();
        if (!pagos.isEmpty())
            return ResponseEntity.badRequest().body(new Mensaje("La parada no puede ser eliminada pues el viaje ya posee un pago registrado"));

        paradaService.delete(model);
        return ResponseEntity.noContent().build();
    }
}
