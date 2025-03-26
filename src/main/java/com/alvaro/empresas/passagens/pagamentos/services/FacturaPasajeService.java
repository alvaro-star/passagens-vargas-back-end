package com.alvaro.empresas.passagens.pagamentos.services;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.FacturaPasajeDTO;
import com.alvaro.empresas.passagens.dtos.pasagens.ContactoDTO;
import com.alvaro.empresas.passagens.enums.TipoPagamento;
import com.alvaro.empresas.passagens.helpers.PasajesPDF;
import com.alvaro.empresas.passagens.models.ContatoModel;
import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaEmpresaModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaPasagemModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.pagamentos.repositories.FacturaPasajeRepository;
import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import com.alvaro.empresas.passagens.services.PrecioService;
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

    public FaturaPasagemModel saveCliente(ContactoDTO contactoDTO, BigDecimal precioTotal, ViagemModel viaje, TipoPagamento metodo) {
        BigDecimal tasa = new BigDecimal("0.1");//Cuanto será cobrad por el servicio
        BigDecimal tasaServicio = precioTotal.multiply(tasa);

        if (!metodo.equals(TipoPagamento.QR))
            throw new ValidationException("metodo", "Metodo de Pago invalido");

        var contactoModel = new ContatoModel(contactoDTO);
        var pago = new FaturaPasagemModel(precioTotal, BigDecimal.ZERO, tasaServicio, false, metodo, viaje, null, contactoModel);
        return facturaPasajeRepository.save(pago);
    }

    public FaturaPasagemModel saveEmpresa(BigDecimal precioTotal, ViagemModel viaje, TipoPagamento metodo, boolean estaPago) {
        LocalDateTime fechaPago = LocalDateTime.now();
        BigDecimal tasaServicio = BigDecimal.ZERO;
        var pago = new FaturaPasagemModel(precioTotal, BigDecimal.valueOf(0), tasaServicio, estaPago, metodo, viaje, fechaPago, null);
        return facturaPasajeRepository.save(pago);
    }

    //O tipo de retorno é vazio, mas estamos colocando booleano por teste

    @Transactional
    public boolean pagarQr(UUID idPago) {//
        FaturaPasagemModel pago = facturaPasajeRepository.findById(idPago).orElseThrow(() -> new ObjectNotFoundException(idPago, FaturaPasagemModel.class.getName()));
        if (pago.getEstaPago()) {
            rembolso();
            mandarEmail("El precio ya fue pagado");
            return false;
        }

        PrecoModel precio = pago.getPassagens().get(0).getPreco();
        List<Integer> sillasVendidas = pasajeRepository.getPasajesVendidosAndNoRembolso(precio.getId());

        int nPasajes = 0;
        for (PassagemModel pasaje : pago.getPassagens()) {
            if (sillasVendidas.contains(pasaje.getNumeroAssento())) {
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
        pago.setEstaPago(true);
        pago.setDataPagamento(LocalDateTime.now());
        facturaPasajeRepository.save(pago);
        return true;
    }

    private static ViagemModel calculateValorArrecadado(FaturaPasagemModel pago) {
        var viaje = pago.getViagem();
        BigDecimal valorTotalPago = pago.getValorTotal() != null ? pago.getValorTotal() : BigDecimal.ZERO;
        if (pago.getPassagens().get(0).getCompradoWeb()) {
            BigDecimal valorArrecadadoWeb = viaje.getValorArrecadadoWeb() != null ? viaje.getValorArrecadadoWeb() : BigDecimal.ZERO;
            viaje.setValorArrecadadoWeb(valorArrecadadoWeb.add(valorTotalPago));
        } else {
            BigDecimal valorArrecadadoNoWeb = viaje.getValorArrecadadoNoWeb() != null ? viaje.getValorArrecadadoNoWeb() : BigDecimal.ZERO;
            viaje.setValorArrecadadoNoWeb(valorArrecadadoNoWeb.add(valorTotalPago));
        }
        return viaje;
    }

    public void codigoVencido(UUID idPago) {//
        FaturaPasagemModel pago = facturaPasajeRepository.findById(idPago).orElseThrow(() -> new ObjectNotFoundException(idPago, FaturaPasagemModel.class.getName()));
        if (!pago.getEstaPago()) {
            for (PassagemModel passagem : pago.getPassagens())
                pasajeRepository.delete(passagem);
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
            throw new ObjectNotFoundException(id, FaturaEmpresaModel.class.getName());
        if (factura.get().getViagem() == null)
            throw new ValidationException("protocolo", "El comprobante posso un viaje null");
        String empresaNombre = factura.get().getViagem().getEmpresa().getNombre();
        ParadaModel salida = factura.get().getPassagens().get(0).getSaida();
        ParadaModel destino = factura.get().getPassagens().get(0).getDestino();
        try {
            for (PassagemModel passagemModel : factura.get().getPassagens())
                pasajesPDF.addPasaje(passagemModel, empresaNombre, salida, destino, factura.get().getMetodoPagamento());
            emptyByteArray = pasajesPDF.closeAndGetBytes();
            return emptyByteArray;
        } catch (IOException exception) {
            throw new ValidationException("pasajes", "Hubo un error ala hora de crear los boletos");
        }
    }

    public Page<FacturaPasajeDTO> findAllFromViaje(UUID idViaje, Pageable pageable) {
        Page<FaturaPasagemModel> models = facturaPasajeRepository.findByViagemId(idViaje, pageable);
        return models.map(FacturaPasajeDTO::new);
    }
}