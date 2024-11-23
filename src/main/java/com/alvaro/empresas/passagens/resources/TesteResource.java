package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.pagos.dtos.RelatorioSolicitudDTO;
import com.alvaro.empresas.passagens.services.PasajeService;
import com.alvaro.empresas.passagens.services.relatorios.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/teste")
public class TesteResource {
    @Autowired
    private RelatorioService relatorioService;
    @Autowired
    private PasajeService pasajeService;


    @GetMapping("/relatorio/{id}")
    public ResponseEntity<byte[]> getRelatorio(@PathVariable UUID id) {
        Date data = new Date(2024, Calendar.NOVEMBER, 15);
        var solicitudDTO = new RelatorioSolicitudDTO(id, data);
        byte[] pasajePdf = relatorioService.makeRelatorioMensual(solicitudDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=pasaje.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return new ResponseEntity<>(pasajePdf, headers, HttpStatus.OK);
    }

    @GetMapping("/pasaje/{id}")
    public ResponseEntity<byte[]> index(@PathVariable UUID id) {
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
