package com.alvaro.empresas.passagens;

import com.alvaro.empresas.passagens.services.relatorios.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@SpringBootApplication
public class PassagensApplication {
    @Autowired
    private RelatorioService relatorioService;

    public static void main(String[] args) {
        SpringApplication.run(PassagensApplication.class, args);
    }

    @GetMapping("/teste/{id}")
    public ResponseEntity<byte[]> index(@PathVariable(value = "id") UUID id) {
        Date data = new Date(2024, Calendar.SEPTEMBER, 15);
        byte[] pasajePdf = relatorioService.makeRelatorioMensual(id, data);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=pasaje.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(pasajePdf, headers, HttpStatus.OK);
    }
}
