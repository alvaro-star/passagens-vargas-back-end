package com.alvaro.empresas.passagens.security.services;

import com.alvaro.empresas.passagens.configurations.exceptions.InternalException.GeneralException;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.enums.TypeSolicitudOperation;
import com.alvaro.empresas.passagens.helpers.beans.MyUserComponent;
import com.alvaro.empresas.passagens.helpers.services.EmailService;
import com.alvaro.empresas.passagens.security.dtos.UsuarioDTOUpdate;
import com.alvaro.empresas.passagens.security.dtos.UsuarioDTOUpdateValidation;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.alvaro.empresas.passagens.security.models.UsuarioSolicitudModel;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import com.alvaro.empresas.passagens.security.repositories.UsuarioSolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioSolicitudRepository usuarioSolicitudRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private MyUserComponent myUserComponent;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioModel save(UsuarioModel usuarioModel) {
        return usuarioRepository.save(usuarioModel);
    }

    public void updateProfile(UsuarioDTOUpdate solicitud) {
        var usuarioModel = myUserComponent.getUserModel();
        boolean emailUsed = false;

        if (solicitud.email() != null && !solicitud.email().isBlank() && !solicitud.email().equals(usuarioModel.getLogin()))
            emailUsed = usuarioRepository.existsByLogin(solicitud.email());

        if (emailUsed) throw new GeneralException("El email esta indisponible");
        LocalDateTime thrityMinutesBefore = LocalDateTime.now().minusMinutes(60);

        var solicitudes = usuarioSolicitudRepository.findByEmailAfterTime(solicitud.email(), thrityMinutesBefore, TypeSolicitudOperation.UPDATE);
        if (solicitudes.size() >= 5)
            throw new GeneralException("Fueron intentadas muchas solicitaciones");

        if (!passwordEncoder.matches(solicitud.contrasena(), usuarioModel.getPassword()))
            throw new GeneralException("La contrasena es invalida");

        var usuarioSolicitud = new UsuarioSolicitudModel(solicitud, usuarioModel, usuarioModel.getPassword(), TypeSolicitudOperation.UPDATE);
        usuarioSolicitudRepository.save(usuarioSolicitud);

        var emailSended = emailService.mandarEmail(usuarioModel.getLogin(), "Cambio de datos del perfil", "Este es tu codigo de verificacion para editar tud datos: \n" + usuarioSolicitud.getId().toString() + "\nNo lo compartas con nadie");
        if (!emailSended) throw new GeneralException(emailService.messageEmailNotSended);
    }

    public void validateUpdate(UsuarioDTOUpdateValidation form) {
        var userLogado = myUserComponent.getUserModel();
        var usuarioSolicitud = usuarioSolicitudRepository.findById(form.codigo());
        if (usuarioSolicitud.isEmpty())
            throw new ValidationException("codigo", "El codigo de verificacion es invalido");
        LocalDateTime thyrtyMinutesBefore = LocalDateTime.now().minusMinutes(30);

        if (usuarioSolicitud.get().getCreatedAt().isBefore(thyrtyMinutesBefore)) {
            usuarioSolicitudRepository.delete(usuarioSolicitud.get());
            throw new ValidationException("codigo", "El codigo ha expirado");
        }

        if (!usuarioSolicitud.get().getEmail().equals(userLogado.getLogin()))
            throw new ValidationException("email", "El codigo no le pertenece a este usuario");

        userLogado.updateValues(usuarioSolicitud.get());
        usuarioRepository.save(userLogado);
    }
}
