package com.alvaro.empresas.passagens.pagamentos.services;

import java.util.HashMap;

import org.springframework.stereotype.Service;

@Service
public class BancoAPIService {
    private final String numberAccountUser = "";
    private final String secretKey = "";
    private final String senha = "";

    public String transferir(String origem, String destino, String valor) {
        return secretKey + senha;
    }

    public String transferir(String destino, String valor) {
        return transferir(numberAccountUser, destino, valor);
    }

    public HashMap<String, String> getPagamentoData(String id) {
        return new HashMap<>();
    }
}