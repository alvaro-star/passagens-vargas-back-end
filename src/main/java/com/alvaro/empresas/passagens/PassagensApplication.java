package com.alvaro.empresas.passagens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController("/familia")
public class PassagensApplication {
    public static void main(String[] args) {
        SpringApplication.run(PassagensApplication.class, args);
    }

    @GetMapping("/")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("Bem vindo");
    }

}
