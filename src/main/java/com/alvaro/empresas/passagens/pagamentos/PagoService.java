package com.alvaro.empresas.passagens.pagamentos;

import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaPasagemModel;
import com.alvaro.empresas.passagens.pagamentos.repositories.FacturaEmpresaRepository;
import com.alvaro.empresas.passagens.pagamentos.repositories.FacturaPasajeRepository;


public class PagoService {
    private FacturaPasajeRepository facturaPasajeRepository;
    private FacturaEmpresaRepository facturaEmpresaRepository;

    private String rembolso(PassagemModel pasaje) {
        return "Eliminado";
    }

    private String generarQR(FaturaPasagemModel factura) {
        return "Teste";
    }
}
