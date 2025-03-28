package com.alvaro.empresas.passagens.helpers.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Value("${spring.mail.username}")
    private String sender;
    public final String messageEmailNotSended = "A mensagem não pôde ser enviada";

    @Autowired
    private JavaMailSender mailSender;

    public boolean mandarEmail(String destino, String assunto, String mensagem) {
        if (!destino.equals(sender)) {
            var message = new SimpleMailMessage();
            message.setTo(destino);
            message.setSubject(assunto);
            message.setText(mensagem);
            message.setFrom(sender);
            mailSender.send(message);
            return true;
        }
        return false;
    }
}
