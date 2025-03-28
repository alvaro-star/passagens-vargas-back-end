package com.alvaro.empresas.passagens.pagamentos.services;

import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaPassagemModel;
import com.alvaro.empresas.passagens.pagamentos.repositories.FaturaEmpresaRepository;
import com.alvaro.empresas.passagens.pagamentos.repositories.FaturaPassagemRepository;


public class PagamentoService {
    private FaturaPassagemRepository faturaPassagemRepository;
    private FaturaEmpresaRepository faturaEmpresaRepository;

    private String rembolso(PassagemModel pasaje) {
        return "Eliminado";
    }

    private String generarQR(FaturaPassagemModel factura) {
        return "Teste";
    }
}
