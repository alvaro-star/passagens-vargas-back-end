package com.alvaro.empresas.passagens;

import com.alvaro.empresas.passagens.onibus.dtos.PisoInputDTO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alvaro.empresas.passagens.dtos.pasagens.PassagensDTOVenta;


import jakarta.validation.Valid;

@SpringBootApplication
@RestController
@RequestMapping("teste")
public class PassagensApplication {
    public static void main(String[] args) {
        SpringApplication.run(PassagensApplication.class, args);
    }

    @PostMapping
    public void hello(@RequestBody @Valid PisoInputDTO teste) {
        System.out.println("Teste");
    }
}
