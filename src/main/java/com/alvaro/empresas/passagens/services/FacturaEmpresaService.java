package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.models.FacturaEmpresaModel;
import com.alvaro.empresas.passagens.models.PasajeModel;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.repositories.FacturaEmpresaRepository;
import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FacturaEmpresaService {
    @Autowired
    private FacturaEmpresaRepository fERepository;
    @Autowired
    private PasajeRepository pasajeRepository;

    public FacturaEmpresaModel findById(UUID id) {
        var model = fERepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, FacturaEmpresaModel.class.getName()));
    }

    public void save(UUID idEmpresa) {
        List<ViajeModel> viajes = new ArrayList<>();
        List<PasajeModel> pasajes = new ArrayList<>();
        BigDecimal sumaCompleta = new BigDecimal("0");
        BigDecimal sumaPrecio = new BigDecimal("0");
        BigDecimal sumaViaje = new BigDecimal("0");

        BigDecimal valorArrecadadoWeb;
        BigDecimal valorArrecadadoNoWeb;
        BigDecimal valorArrecadadoEfectivo;

        BigDecimal sumaASerPaga = new BigDecimal("0");
        for (ViajeModel viaje : viajes) {
            valorArrecadadoWeb = BigDecimal.ZERO;
            valorArrecadadoNoWeb = BigDecimal.ZERO;
            valorArrecadadoEfectivo = BigDecimal.ZERO;

            for (PrecioModel precio : viaje.getPrecios()) {
                pasajes = pasajeRepository.findByPrecioIdAndEstaPagado(precio.getId(), true);
                for (PasajeModel pasaje : pasajes) {
                    if (!pasaje.getEnEfectivo())
                        sumaASerPaga = sumaASerPaga.add(pasaje.getPrecioPagado());
                    if (pasaje.getCompradoWeb()) {
                        valorArrecadadoWeb = valorArrecadadoWeb.add(pasaje.getPrecioPagado());
                    } else {
                        valorArrecadadoNoWeb = valorArrecadadoNoWeb.add(pasaje.getPrecioPagado());
                        if (pasaje.getEnEfectivo())
                            valorArrecadadoEfectivo = valorArrecadadoEfectivo.add(pasaje.getPrecioPagado());
                    }

                }
            }
            if (!viaje.getValorArrecadadoWeb().equals(valorArrecadadoWeb) || !viaje.getValorArrecadadoEfectivo().equals(valorArrecadadoEfectivo) || !viaje.getValorArrecadadoNoWeb().equals(valorArrecadadoNoWeb))
                System.out.println("Uno delos valores no fue calculado correctamente");
        }
    }

}
