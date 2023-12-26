package com.alvaro.empresas.passagens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController("/familia")
public class PassagensApplication {
    public static void main(String[] args) {
        SpringApplication.run(PassagensApplication.class, args);
    }

    @GetMapping("/")
    public String teste() {
        return "Chuchu";
    }

    @GetMapping("/jose")
    public String jose(){
        return "El chuchu";
    }

    @GetMapping("/alvaro")
    public String alvaro(){
        return "El Barry";
    }
    @GetMapping("/carla")
    public String carla(){
        return "El pepe";
    }

    @GetMapping("/oscar")
    public String oscar(){
        return "El Bob";
    }
    @GetMapping("/neroly")
    public String nolas(){
        return "La nolas";
    }

}
