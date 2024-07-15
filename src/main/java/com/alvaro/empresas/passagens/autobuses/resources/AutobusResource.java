package com.alvaro.empresas.passagens.autobuses.resources;

import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTOList;
import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTOResponse;
import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.repositories.AutobusRepository;
import com.alvaro.empresas.passagens.autobuses.services.AutobusService;
import com.alvaro.empresas.passagens.autobuses.services.validacao.ValidarPiso;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.helpers.beans.MyUserService;
import com.alvaro.empresas.passagens.services.EmpresaService;
import com.alvaro.empresas.passagens.services.validacao.ValidationErrorsWithList;
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
    private final AutobusService autobusService;
    private final MyUserService myUserService;
    private final EmpresaService empresaService;
    private final AutobusRepository autobusRepository;

    @Autowired
    public AutobusResource(
            AutobusService autobusService,
            MyUserService myUserService,
            EmpresaService empresaService,
            AutobusRepository autobusRepository
    ) {
        this.autobusService = autobusService;
        this.myUserService = myUserService;
        this.empresaService = empresaService;
        this.autobusRepository = autobusRepository;
    }

    private Mensaje validarUsuario(UUID idEmpresa) {
        var user = myUserService.getUser();
        if (user.getIdEmpresa() == null)
            return new Mensaje("Usted no esta relacionado a una empresa");
        var empresa = empresaService.findById(user.getIdEmpresa());
        if (empresa.getBloqued())
            return new Mensaje("La empresa esta bloqueada");
        if (!user.isMyEmpresa(idEmpresa))
            return new Mensaje("Usted no esta relacionado a esta empresa");
        return new Mensaje("");
    }

    @GetMapping("/from/{idEmpresa}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Page<AutobusDTOList>> getAutobusesFromEmpresa(@PathVariable(value = "idEmpresa") UUID id,
                                                                        @PageableDefault(size = 10, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable) {
        var user = myUserService.getUser();
        if (!(user.hasRole("ROLE_ADMIN") || user.isMyEmpresa(id))) {
            Page<AutobusDTOList> emptyPage = Page.empty(pageable);
            return ResponseEntity.ok().body(emptyPage);
        }
        return ResponseEntity.ok().body(autobusService.findAllFromEmpresa(id, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutobusDTOResponse> getOne(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok().body(autobusService.getOne(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> save(@RequestBody @Valid AutobusDTO dto, BindingResult bindingResult) {
        ValidationErrorsWithList validacao = ValidarPiso.validarAutobusDTO(bindingResult, dto, autobusRepository);
        if (!validacao.getErrors().isEmpty() || !validacao.getErrorsList().isEmpty())
            return ResponseEntity.unprocessableEntity().body(validacao);
        Mensaje mensaje = this.validarUsuario(dto.idEmpresa());
        if (mensaje.conteudo().isEmpty())
            return ResponseEntity.status(HttpStatus.CREATED).body(autobusService.salvar(dto));
        else
            return ResponseEntity.badRequest().body(mensaje);
    }

    //Solo el administrador
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> update(@PathVariable(value = "id") Integer id, @Valid @RequestBody AutobusDTOUpdate dto, BindingResult bindingResult) {
        var transform = new AutobusDTO(dto.placa());
        ValidationErrorsWithList validacao = ValidarPiso.validarAutobusDTO(bindingResult, transform, autobusRepository);
        if (!validacao.getErrors().isEmpty() || !validacao.getErrorsList().isEmpty())
            return ResponseEntity.unprocessableEntity().body(validacao);

        var autobus = autobusService.findById(id);
        Mensaje mensaje = this.validarUsuario(autobus.getEmpresa().getId());
        if (mensaje.conteudo().isEmpty())
            return ResponseEntity.ok().body(autobusService.update(dto, autobus));
        else
            return ResponseEntity.badRequest().body(mensaje);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") Integer id) {
        var model = autobusService.findById(id);
        Mensaje mensaje = this.validarUsuario(model.getEmpresa().getId());
        if (mensaje.conteudo().isEmpty()) {
            autobusService.delete(model);
            return ResponseEntity.noContent().build();
        } else
            return ResponseEntity.badRequest().body(mensaje);
    }
}
