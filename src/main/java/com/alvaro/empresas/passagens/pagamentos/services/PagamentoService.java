package com.alvaro.empresas.passagens.pagamentos.services;

import org.springframework.beans.factory.annotation.Autowired;

import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaPassagemModel;
import com.alvaro.empresas.passagens.pagamentos.repositories.FaturaEmpresaRepository;
import com.alvaro.empresas.passagens.pagamentos.repositories.FaturaPassagemRepository;

public class PagamentoService {
    @Autowired
    private FaturaPassagemRepository faturaPassagemRepository;
    @Autowired
    private FaturaEmpresaRepository faturaEmpresaRepository;

    private String rembolso(PassagemModel pasaje) {
        return "Eliminado";
    }

    private String generarQR(FaturaPassagemModel fatura) {
        return "Teste";
    }
}
