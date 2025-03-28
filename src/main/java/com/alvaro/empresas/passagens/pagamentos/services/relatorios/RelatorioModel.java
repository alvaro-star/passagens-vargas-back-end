package com.alvaro.empresas.passagens.pagamentos.services.relatorios;

import java.util.HashMap;
import java.util.List;

import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;

import lombok.Data;
@Data
public class RelatorioModel {
    private EmpresaModel empresa;
    private int nMes, nAno;
    private List<LugarModel> lugaresSaida, lugaresDestino;
    protected Integer nViagens, nViagensCanceladas;
    protected Integer nPassagensTotal, nPassagensCanceladas;
    private double valorArrecadadoWeb, valorArrecadadoNaoWeb;
    private HashMap<String, HashMetodoPagamentoValor> dinheiroPorMetodoWeb, dinheiroPorMetodoNaoWeb;

    public RelatorioModel(EmpresaModel empresa) {
        this.nViagens = 0;
        nViagensCanceladas = 0;
        nPassagensTotal = 0;
        nPassagensCanceladas = 0;
        valorArrecadadoWeb = 0;
        valorArrecadadoNaoWeb = 0;
        this.empresa = empresa;
    }
}