package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.FacturaPasajeDTO;
import com.alvaro.empresas.passagens.dtos.pasajes.ContactoDTO;
import com.alvaro.empresas.passagens.enums.MetodoPagamentoEnum;
import com.alvaro.empresas.passagens.helpers.PasajesPDF;
import com.alvaro.empresas.passagens.models.*;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.repositories.FacturaPasajeRepository;
import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FacturaPasajeService {
    @Autowired
    private FacturaPasajeRepository facturaPasajeRepository;
    @Autowired
    private PasajeRepository pasajeRepository;
    @Autowired
    private PrecioService precioService;
    @Autowired
    private ViajeRepository viajeRepository;

    public FacturaPasajeModel save(ContactoDTO contactoDTO, BigDecimal precioTotal, ViajeModel viaje, MetodoPagamentoEnum metodo, boolean guardarContacto) {
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
            contactoModel = new ContactoModel(contactoDTO.nombre(), contactoDTO.email(), contactoDTO.telefono());

        var pago = new FacturaPasajeModel(precioTotal, BigDecimal.valueOf(0), tasaServicio, estaPagado, metodo, viaje, fechaPago, contactoModel);

        return facturaPasajeRepository.save(pago);
    }

    //O tipo de retorno é vazio, mas estamos colocando booleano por teste

    @Transactional
    public boolean pagarQr(UUID idPago) {//
        FacturaPasajeModel pago = facturaPasajeRepository.findById(idPago).orElseThrow(() -> new ObjectNotFoundException(idPago, FacturaPasajeModel.class.getName()));
        if (pago.getEstaPagado()) {
            rembolso();
            mandarEmail("El precio ya fue pagado");
            return false;
        }

        PrecioModel precio = pago.getPasajes().get(0).getPrecio();
        List<Integer> sillasVendidas = pasajeRepository.getPasajesVendidosAndNoRembolso(precio.getId());

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


        pasajeRepository.updateValuePagado(pago.getId(), true);

        var viaje = calculateValorArrecadado(pago);

        viajeRepository.save(viaje);

        pago.setEstaPagado(true);
        pago.setFechaPago(LocalDateTime.now());
        facturaPasajeRepository.save(pago);
        return true;
    }

    private static ViajeModel calculateValorArrecadado(FacturaPasajeModel pago) {
        var viaje = pago.getViaje();
        BigDecimal valorTotalPago = pago.getValorTotal() != null ? pago.getValorTotal() : BigDecimal.ZERO;
        if (pago.getPasajes().get(0).getCompradoWeb()) {
            BigDecimal valorArrecadadoWeb = viaje.getValorArrecadadoWeb() != null ? viaje.getValorArrecadadoWeb() : BigDecimal.ZERO;
            viaje.setValorArrecadadoWeb(valorArrecadadoWeb.add(valorTotalPago));
        } else {
            BigDecimal valorArrecadadoNoWeb = viaje.getValorArrecadadoNoWeb() != null ? viaje.getValorArrecadadoNoWeb() : BigDecimal.ZERO;
            viaje.setValorArrecadadoNoWeb(valorArrecadadoNoWeb.add(valorTotalPago));
        }
        return viaje;
    }

    public void codigoVencido(UUID idPago) {//
        FacturaPasajeModel pago = facturaPasajeRepository.findById(idPago).orElseThrow(() -> new ObjectNotFoundException(idPago, FacturaPasajeModel.class.getName()));
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

    public byte[] downloadFactura(UUID id) {
        var factura = facturaPasajeRepository.findById(id);
        PasajesPDF pasajesPDF = new PasajesPDF();
        byte[] emptyByteArray = new byte[0];

        if (!factura.isPresent())
            throw new ObjectNotFoundException(id, FacturaEmpresaModel.class.getName());
        if (factura.get().getViaje() == null)
            throw new ValidationException("protocolo", "El comprobante posso un viaje null");
        String empresaNombre = factura.get().getViaje().getEmpresa().getNombre();
        ParadaModel salida = factura.get().getPasajes().get(0).getSalida();
        ParadaModel destino = factura.get().getPasajes().get(0).getDestino();
        try {
            for (PasajeModel pasajeModel : factura.get().getPasajes())
                pasajesPDF.addPasaje(pasajeModel, empresaNombre, salida, destino, factura.get().getMetodoPago());
            emptyByteArray = pasajesPDF.closeAndGetBytes();
            return emptyByteArray;
        } catch (IOException exception) {
            throw new ValidationException("pasajes", "Hubo un error ala hora de crear los boletos");
        }
    }

    public Page<FacturaPasajeDTO> findAllFromViaje(UUID idViaje, Pageable pageable) {
        Page<FacturaPasajeModel> models = facturaPasajeRepository.findByViajeCodigo(idViaje, pageable);
        return models.map(FacturaPasajeDTO::new);
    }
}