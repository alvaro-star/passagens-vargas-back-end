package com.alvaro.empresas.passagens;

import com.alvaro.empresas.passagens.security.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class PassagensApplication {
    public static void main(String[] args) {
        SpringApplication.run(PassagensApplication.class, args);
    }

    @GetMapping("/")
    public String teste() {
        return "Teste";
    }

}
