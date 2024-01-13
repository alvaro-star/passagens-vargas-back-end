package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTO;
import com.alvaro.empresas.passagens.models.PagoModel;
import com.alvaro.empresas.passagens.models.PasajeModel;
import com.alvaro.empresas.passagens.repositories.PagoRepository;
import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PagoService {
    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private PasajeRepository pasajeRepository;

    public PagoModel save(PasajesDTO dto, Float precio) {
        Float precioTotal = dto.pasajes().size() * precio;
        PagoModel pago = new PagoModel();
        pago.setValorTotal(precioTotal);
        //pago.setDescuento(dto.descuento());
        pago.setDescuento(0f);
        var tasa = precioTotal / 10;

        pago.getContacto().setEmail(dto.contacto().email());
        pago.getContacto().setTelefono(dto.contacto().telefono());

        pago.setTasaServicio(tasa);
        pago.setEstaPagado(true);

        return pagoRepository.save(pago);
    }

    @Transactional
    public void pagarQr(UUID idPago) {//
        PagoModel pago = pagoRepository.findById(idPago).orElseThrow(() -> new ObjectNotFoundException(idPago, PagoModel.class.getName()));
        if (pago.getEstaPagado()) {
            rembolso();
            mandarEmail("El precio ya fue pagado");
            pagoRepository.delete(pago);
        }
        for (PasajeModel pasaje : pago.getPasajes()) {
            if (pasaje.getEstaPagado()) {
                rembolso();
                mandarEmail("Una delas sillas ya fue pagado");
                pagoRepository.delete(pago);
                break;
            }
        }

        for (PasajeModel pasaje : pago.getPasajes()) {
            pasajeRepository.updateValuePagado(pasaje.getId(), true);
        }
        pago.setEstaPagado(true);
        pago.setFechaPago(LocalDateTime.now());
        pagoRepository.save(pago);
    }

    public void codigoVencido(UUID idPago) {//
        PagoModel pago = pagoRepository.findById(idPago).orElseThrow(() -> new ObjectNotFoundException(idPago, PagoModel.class.getName()));
        if (!pago.getEstaPagado()) {
            for (PasajeModel pasaje : pago.getPasajes()) {
                pasajeRepository.delete(pasaje);
            }
        }
    }

    public void rembolso() {

    }

    public void mandarEmail(String mensaje) {

    }
}