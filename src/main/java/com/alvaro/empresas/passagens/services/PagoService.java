package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTO;
import com.alvaro.empresas.passagens.enums.MetodoPagamentoEnum;
import com.alvaro.empresas.passagens.models.*;
import com.alvaro.empresas.passagens.repositories.ContactoRepository;
import com.alvaro.empresas.passagens.repositories.PagoRepository;
import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
    private ViajeRepository viajeRepository;

    public PagoModel save(PasajesDTO dto, BigDecimal precio, ViajeModel viaje, MetodoPagamentoEnum metodo, boolean guardarContacto) {
        int nPasajes = dto.pasajes().size();
        BigDecimal precioTotal = precio.multiply(BigDecimal.valueOf(nPasajes));
        boolean estaPagado;

        LocalDateTime fechaPago = null;

        BigDecimal tasa = new BigDecimal("0.1");//Cuanto será cobrad por el servicio

        BigDecimal tasaServicio = new BigDecimal("0.00");

        if (metodo == MetodoPagamentoEnum.QR) {
            tasaServicio = precioTotal.multiply(tasa);
            estaPagado = false;
        } else if (metodo == MetodoPagamentoEnum.EFECTIVO) {
            estaPagado = true;
            fechaPago = LocalDateTime.now();
        } else
            throw new ValidationException(new FieldMessage("metodo", "Metodo de Pago invalido"));

        ContactoModel contactoModel = null;
        if (guardarContacto)
            contactoModel = new ContactoModel(dto.contacto().nombre(), dto.contacto().email(), dto.contacto().telefono());

        var pago = new PagoModel(precioTotal, BigDecimal.valueOf(0), tasaServicio, estaPagado, metodo, viaje, fechaPago, contactoModel);

        return pagoRepository.save(pago);
    }

    //O tipo de retorno é vazio, mas estamos colocando booleano por teste

    @Transactional
    public boolean pagarQr(UUID idPago) {//
        PagoModel pago = pagoRepository.findById(idPago).orElseThrow(() -> new ObjectNotFoundException(idPago, PagoModel.class.getName()));
        if (pago.getEstaPagado()) {
            rembolso();
            mandarEmail("El precio ya fue pagado");
            return false;
        }

        PrecioModel precio = pago.getPasajes().get(0).getPrecio();
        List<Integer> sillasVendidas = pasajeRepository.getPasajesVendidos(precio.getId());

        int nPasajes = 0;
        for (PasajeModel pasaje : pago.getPasajes()) {
            if (sillasVendidas.contains(pasaje.getNSilla())) {
                rembolso();
                mandarEmail("Una delas sillas ya fue pagado, el pago fue cancelado");
                return false;
            }
            //Pode ser melhorado
            nPasajes++;
        }

        int nSillasDisponibles = precio.getNSillasDisponibles() - nPasajes;
        if (nSillasDisponibles == 0) {
            precio.setNSillasDisponibles(0);
            precio.setLleno(true);
        } else if (nSillasDisponibles > 0) {
            precio.setNSillasDisponibles(nSillasDisponibles);
        } else {
            mandarEmail("No hay sillas disponibles");
            return false;
        }

        precioService.updateFromService(precio);

        for (PasajeModel pasaje : pago.getPasajes())
            pasajeRepository.updateValuePagado(pasaje.getId(), true);

        var viaje = pago.getViaje();
        BigDecimal valorArrecadado = viaje.getValorArrecadadoWeb() != null ? viaje.getValorArrecadadoWeb() : BigDecimal.ZERO;
        BigDecimal valorTotalPago = pago.getValorTotal() != null ? pago.getValorTotal() : BigDecimal.ZERO;
        viaje.setValorArrecadadoWeb(valorArrecadado.add(valorTotalPago));
        viajeRepository.save(viaje);

        pago.setEstaPagado(true);
        pago.setFechaPago(LocalDateTime.now());
        pagoRepository.save(pago);
        return true;
    }

    public void codigoVencido(UUID idPago) {//
        PagoModel pago = pagoRepository.findById(idPago).orElseThrow(() -> new ObjectNotFoundException(idPago, PagoModel.class.getName()));
        if (!pago.getEstaPagado()) {
            for (PasajeModel pasaje : pago.getPasajes())
                pasajeRepository.delete(pasaje);
        }
    }

    public void generarQr(Float valor) {
    }

    public void rembolso() {
    }

    public void mandarEmail(String mensaje) {

    }
}