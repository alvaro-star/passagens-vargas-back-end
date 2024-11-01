package com.alvaro.empresas.passagens;

import com.alvaro.empresas.passagens.helpers.thymeleaf.PDFThymeleaf;
import com.alvaro.empresas.passagens.services.PasajeService;
import com.alvaro.empresas.passagens.services.relatorios.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@SpringBootApplication
@RestController
@RequestMapping("/thymeleaf")
public class PassagensApplication {
    @Autowired
    private RelatorioService relatorioService;
    @Autowired
    private PasajeService pasajeService;
    @Autowired
    private PDFThymeleaf pdfThymeleaf;

    public static void main(String[] args) {
        SpringApplication.run(PassagensApplication.class, args);
    }

    @GetMapping("/relatorio/{id}")
    public ResponseEntity<byte[]> index(@PathVariable(value = "id") UUID id, Model model) {
        Date data = new Date(2024, Calendar.SEPTEMBER, 15);
        byte[] pasajePdf = relatorioService.makeRelatorioMensual(id, data, model);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=pasaje.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(pasajePdf, headers, HttpStatus.OK);
    }

    @GetMapping("/pasaje/{id}")
    public ResponseEntity<byte[]> index(@PathVariable(value = "id") UUID id) {
        var pasajePdf = pasajeService.getOnePasajeDownload(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=pasaje.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(pasajePdf, headers, HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<String> teste() {
        return ResponseEntity.ok("Hello");
    }
}
