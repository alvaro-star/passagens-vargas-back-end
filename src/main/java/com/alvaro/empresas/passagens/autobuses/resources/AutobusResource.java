package com.alvaro.empresas.passagens.autobuses.resources;

import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTOResponse;
import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.services.AutobusService;
import com.alvaro.empresas.passagens.autobuses.services.validacao.ValidarPiso;
import com.alvaro.empresas.passagens.helpers.Mensaje;
import com.alvaro.empresas.passagens.helpers.beans.MyUserComponent;
import com.alvaro.empresas.passagens.models.EmpresaModel;
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
    @Autowired
    private AutobusService autobusService;
    @Autowired
    private ValidarPiso validarPiso;
    @Autowired
    private MyUserComponent myUserComponent;
    @Autowired
    private EmpresaService empresaService;

    private Mensaje validarUsuario(EmpresaModel empresa) {
        var user = myUserComponent.getUser();
        // if (user.getIdEmpresa() == null) return new Mensaje("Usted no esta relacionado a una empresa");
        if (empresa.getBloqued() || !empresa.getEnabled()) return new Mensaje("La empresa esta bloqueada");
        if (!user.isMyEmpresa(empresa.getId())) return new Mensaje("Usted no esta relacionado a esta empresa");
        return new Mensaje("");
    }

    @GetMapping("/from/{idEmpresa}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN', 'ROLE_EMPRESA_FUNCIONARIO')")
    public ResponseEntity<Page<AutobusDTOResponse>> getAutobusesFromEmpresa(@PathVariable UUID idEmpresa, @PageableDefault(size = 10, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable) {
        var user = myUserComponent.getUser();
        if (!user.isAdminOrOwnerEmpresa(idEmpresa)) return ResponseEntity.ok().body(Page.empty(pageable));
        return ResponseEntity.ok().body(autobusService.findAllFromEmpresa(idEmpresa, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutobusDTOResponse> getOne(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok().body(autobusService.getOne(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> save(@RequestBody @Valid AutobusDTO dto, BindingResult bindingResult) {
        ValidationErrorsWithList validacao = validarPiso.validarAutobusDTO(bindingResult, dto);
        if (!validacao.getErrors().isEmpty() || !validacao.getErrorsList().isEmpty())
            return ResponseEntity.unprocessableEntity().body(validacao);
        var empresa = empresaService.findById(dto.idEmpresa());
        Mensaje validationResponse = this.validarUsuario(empresa);
        if (!validationResponse.conteudo().isEmpty()) return ResponseEntity.badRequest().body(validationResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(autobusService.salvar(dto, empresa));
    }

    //Solo el administrador
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> update(@PathVariable(value = "id") Integer id, @Valid @RequestBody AutobusDTOUpdate dto, BindingResult bindingResult) {
        var transform = new AutobusDTO(dto.placa());
        ValidationErrorsWithList validacao = validarPiso.validarAutobusDTO(bindingResult, transform);
        if (!validacao.getErrors().isEmpty() || !validacao.getErrorsList().isEmpty())
            return ResponseEntity.unprocessableEntity().body(validacao);

        var autobus = autobusService.findById(id);
        Mensaje mensaje = this.validarUsuario(autobus.getEmpresa());
        if (!mensaje.conteudo().isEmpty()) return ResponseEntity.badRequest().body(mensaje);
        return ResponseEntity.ok().body(autobusService.update(dto, autobus));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") Integer id) {
        var model = autobusService.findById(id);
        Mensaje mensaje = this.validarUsuario(model.getEmpresa());
        if (!mensaje.conteudo().isEmpty()) return ResponseEntity.badRequest().body(mensaje);

        String resposta = autobusService.delete(model);
        if (resposta.isBlank()) return ResponseEntity.noContent().build();
        else return ResponseEntity.badRequest().body(new Mensaje(resposta));

    }
}
