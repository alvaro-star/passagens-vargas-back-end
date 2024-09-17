package com.alvaro.empresas.passagens.pagos;

import com.alvaro.empresas.passagens.models.PasajeModel;
import com.alvaro.empresas.passagens.pagos.models.FacturaPasajeModel;
import com.alvaro.empresas.passagens.repositories.FacturaEmpresaRepository;
import com.alvaro.empresas.passagens.repositories.FacturaPasajeRepository;


public class PagoService {
    private FacturaPasajeRepository facturaPasajeRepository;
    private FacturaEmpresaRepository facturaEmpresaRepository;

    private String rembolso(PasajeModel pasaje) {

        return "Eliminado";
    }

    private String generarQR(FacturaPasajeModel factura) {
        return "Teste";
    }
}
