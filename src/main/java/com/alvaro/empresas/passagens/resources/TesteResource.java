package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.pagos.dtos.RelatorioSolicitudDTO;
import com.alvaro.empresas.passagens.repositories.EmpresaRepository;
import com.alvaro.empresas.passagens.services.PasajeService;
import com.alvaro.empresas.passagens.services.relatorios.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/teste")
public class TesteResource {
    @Autowired
    private RelatorioService relatorioService;
    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private PasajeService pasajeService;


    @GetMapping("/relatorio")
    public ResponseEntity<byte[]> getRelatorio() {
        var pageable = PageRequest.of(0, 1);
        var page = empresaRepository.findAll(pageable);
        Date data = new Date();

        var solicitudDTO = new RelatorioSolicitudDTO(page.getContent().get(0).getId(), data);
        byte[] pasajePdf = new byte[0];
        try {
            System.out.println("Iniciando relatorio");
             pasajePdf = relatorioService.makeRelatorioMensual(solicitudDTO);
            System.out.println("Finalizando relatorio");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }


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
