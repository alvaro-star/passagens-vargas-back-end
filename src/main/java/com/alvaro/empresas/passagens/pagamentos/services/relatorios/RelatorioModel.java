package com.alvaro.empresas.passagens.pagamentos.services.relatorios;

import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class RelatorioModel {
    private EmpresaModel empresa;
    private int nMes, nAno;
    private List<LugarModel> lugaresSalida, lugaresDestino;
    protected Integer nViajes, nViajesCancelados;
    protected Integer nPasajesTotal, nPasajesCancelados;
    private double valorArrecadadoWeb, valorArrecadadoNoWeb;
    private HashMap<String, HashMetodoPagamentoValor> dineroPorMetodoWeb, dineroPorMetodoNoWeb;

    public RelatorioModel(EmpresaModel empresa) {
        this.nViajes = 0;
        nViajesCancelados = 0;
        nPasajesTotal = 0;
        nPasajesCancelados = 0;
        valorArrecadadoWeb = 0;
        valorArrecadadoNoWeb = 0;
        this.empresa = empresa;
    }
}
