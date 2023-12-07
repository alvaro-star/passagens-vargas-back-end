package com.alvaro.empresas.passagens.resources;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes")
public class ClienteResource {

    @GetMapping("/Teste")
    public String teste() {
        return "Teste";
    }
}
