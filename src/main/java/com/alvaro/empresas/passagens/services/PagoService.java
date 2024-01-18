package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTO;
import com.alvaro.empresas.passagens.enums.MetodoPagamentoEnum;
import com.alvaro.empresas.passagens.models.ContactoModel;
import com.alvaro.empresas.passagens.models.PagoModel;
import com.alvaro.empresas.passagens.models.PasajeModel;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.repositories.ContactoRepository;
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
    @Autowired
    private PrecioService precioService;
    @Autowired
    private ContactoRepository contactoRepository;

    public PagoModel save(PasajesDTO dto, Float precio, MetodoPagamentoEnum metodo, boolean guardarContacto) {
        PagoModel pago = new PagoModel();
        Float precioTotal = dto.pasajes().size() * precio;
        pago.setValorTotal(precioTotal);

        //pago.setDescuento(dto.descuento());
        pago.setDescuento(0f);

        if (metodo == MetodoPagamentoEnum.QR) {
            var tasa = precioTotal / 10;
            pago.setTasaServicio(tasa);
            pago.setEstaPagado(false);
        } else if (metodo == MetodoPagamentoEnum.EFECTIVO) {
            pago.setTasaServicio(0f);
            pago.setEstaPagado(true);
            pago.setFechaPago(LocalDateTime.now());
        } else {
            throw new ValidationException(new FieldMessage("metodo", "Metodo de Pago invalido"));
        }

        pago.setMetodoPago(metodo);
        var pagoModel = pagoRepository.save(pago);
        if (guardarContacto) {
            ContactoModel contactoModel = new ContactoModel(dto.contacto().email(), dto.contacto().telefono());
            contactoModel.setPago(pagoModel);
            contactoRepository.save(contactoModel);
        }

        return pagoModel;
    }

    //En desarrollo
    @Transactional
    public void pagarQr(UUID idPago) {//
        PagoModel pago = pagoRepository.findById(idPago).orElseThrow(() -> new ObjectNotFoundException(idPago, PagoModel.class.getName()));
        if (pago.getEstaPagado()) {
            rembolso();
            mandarEmail("El precio ya fue pagado");
            return;
        }
        int nPasajes = 0;
        for (PasajeModel pasaje : pago.getPasajes()) {
            if (pasaje.getEstaPagado()) {
                rembolso();
                mandarEmail("Una delas sillas ya fue pagado, el pago fue cancelado");
                return;
            }
            nPasajes++;
        }

        PrecioModel precio = pago.getPasajes().get(0).getPrecio();

        int nSillasDisponibles = precio.getNSillasDisponibles() - nPasajes;
        if (nSillasDisponibles == 0) {
            precio.setNSillasDisponibles(0);
            precio.setLleno(true);
        } else if (nSillasDisponibles > 0) {
            precio.setNSillasDisponibles(nSillasDisponibles);
        } else {
            mandarEmail("No hay sillas disponibles");
            return;
        }

        precioService.updateFromService(precio);

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

    public void generarQr(Float valor) {

    }

    public void rembolso() {

    }

    public void mandarEmail(String mensaje) {

    }
}