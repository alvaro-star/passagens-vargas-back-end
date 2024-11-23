package com.alvaro.empresas.passagens;

import com.alvaro.empresas.passagens.pagos.dtos.RelatorioSolicitudDTO;
import com.alvaro.empresas.passagens.services.PasajeService;
import com.alvaro.empresas.passagens.services.relatorios.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@SpringBootApplication
public class PassagensApplication {
    public static void main(String[] args) {
        SpringApplication.run(PassagensApplication.class, args);
    }
}
