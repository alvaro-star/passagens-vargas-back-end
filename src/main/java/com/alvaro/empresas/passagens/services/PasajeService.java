package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.onibus.models.PisoModel;

import com.alvaro.empresas.passagens.dtos.pasagens.PasagemDTO;
import com.alvaro.empresas.passagens.dtos.pasagens.PasagemDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.pasagens.PaagensDTO;
import com.alvaro.empresas.passagens.dtos.pasagens.PasagensDTOVenta;
import com.alvaro.empresas.passagens.enums.TipoPagamento;
import com.alvaro.empresas.passagens.helpers.PasajesPDF;
import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaPasagemModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaReembolsoModel;
import com.alvaro.empresas.passagens.pagamentos.services.FacturaPasajeService;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import com.alvaro.empresas.passagens.services.validacao.ValidarCompraPasajes;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PasajeService {
    @Value("${api.viaje.min-time-before-buy-pasaje-min}")
    private Integer minTimeBeforeBuyPasaje;
    @Autowired
    private PasajeRepository pasajeRepository;
    @Autowired
    private PrecioService precioService;
    @Autowired
    private FacturaPasajeService facturaPasajeService;
    @Autowired
    private ViajeRepository viajeRepository;
    @Autowired
    private ValidarCompraPasajes validarCompraPasajes;

    private static final Logger logger = LoggerFactory.getLogger(PasajeService.class);

    public PassagemModel findById(UUID id) {
        var model = pasajeRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, PassagemModel.class.getName()));
    }

    public PasagemDTOEmpresaResponse getOne(UUID id) {
        var model = findById(id);
        return new PasagemDTOEmpresaResponse(model);
    }

    public byte[] getOnePasajeDownload(UUID idPasaje) {
        var pasajeModel = findById(idPasaje);
        PasajesPDF pasajePDF = new PasajesPDF();
        byte[] emptyByteArray = new byte[0];
        try {
            ParadaModel salida = pasajeModel.getSaida();
            ParadaModel destino = pasajeModel.getDestino();
            pasajePDF.addPasaje(pasajeModel, pasajeModel.getPreco().getEmpresa().getNombre(), salida, destino, pasajeModel.getFaturaPasagem().getMetodoPagamento());
            emptyByteArray = pasajePDF.closeAndGetBytes();
            return emptyByteArray;
        } catch (IOException exception) {
            throw new ValidationException("pasaje", "Hubo un error ala hora de crear el pasaje");
        }
    }

    private void validarViaje(ViagemModel viaje, Integer idLugarSalida, Integer idLugarDestino) {
        var salida = viaje.getParadaByLugarId(idLugarSalida);
        var destino = viaje.getParadaByLugarId(idLugarDestino);
        if (salida == null)
            throw new ValidationException("idLugarSalida", "La salida no hace parte del trayecto");
        else if (salida.getDataHora().isBefore(LocalDateTime.now().minusMinutes(minTimeBeforeBuyPasaje)))
            throw new RestRuntimeException("El autobus ya inicio el viaje");
        if (destino == null) throw new ValidationException("idLugarDestino", "El destino no hace parte del trayecto");
    }

    //Exclusivo para el servicio online
    @Transactional
    public FaturaPasagemModel saveCliente(PaagensDTO dto, BindingResult bindingResult) {
        var precio = precioService.findById(dto.idPrecio());
        validarCompraPasajes.validarPasajesDTO(bindingResult, dto, "/pasajes");
        var viaje = precio.getViagem();

        validarViaje(viaje, dto.idLugarSalida(), dto.idLugarDestino());

        ParadaModel salida = viaje.getParadaByLugarId(dto.idLugarSalida());
        ParadaModel destino = viaje.getParadaByLugarId(dto.idLugarDestino());

        PisoModel pisoEscolhido = viaje.getAutobus().getPisoByNumero(precio.getNPiso());
        validarSillas(pisoEscolhido, precio, dto.pasajes());

        BigDecimal valorTotal = precio.getPrecio().multiply(BigDecimal.valueOf(dto.pasajes().size()));
        FaturaPasagemModel pago = facturaPasajeService.saveCliente(dto.contacto(), valorTotal, null, TipoPagamento.QR);

        viaje.addValorArrecadadoWeb(pago.getValorTotal());

        viajeRepository.save(viaje);

        PassagemModel passagemModel;
        List<PassagemModel> pasajesList = new ArrayList<>();

        for (PasagemDTO pasagemDTO : dto.pasajes()) {
            passagemModel = new PassagemModel(pasagemDTO, true, precio.getPrecio(), false, false, salida, destino, precio, pago);
            pasajesList.add(passagemModel);
        }

        pasajeRepository.saveAll(pasajesList);
        return pago;
    }


    @Transactional
    public UUID saveEmpresa(PasagensDTOVenta dto, ViagemModel viaje, BindingResult bindingResult) {
        validarCompraPasajes.validarPasajesDTOVenta(bindingResult, dto, "/pasajes/vender");
        ParadaModel salida;
        ParadaModel destino;

        validarViaje(viaje, dto.idLugarSalida(), dto.idLugarDestino());

        salida = viaje.getParadaByLugarId(dto.idLugarSalida());
        destino = viaje.getParadaByLugarId(dto.idLugarDestino());

        List<PasagemDTO> sillasPiso1 = new ArrayList<>(), sillasPiso2 = new ArrayList<>();

        PisoModel piso1 = viaje.getAutobus().getPisoByNumero(1);
        PisoModel piso2 = viaje.getAutobus().getPisoByNumero(2);

        for (PasagemDTO pasajeFor : dto.pasajes()) {
            if (pasajeFor.numeroAssento() > 0 && pasajeFor.numeroAssento() <= piso1.getNSillas())
                sillasPiso1.add(pasajeFor);
            else sillasPiso2.add(pasajeFor);
        }

        PrecoModel precio1 = viaje.getPrecioByNPiso(1);
        PrecoModel precio2 = viaje.getPrecioByNPiso(2);

        BigDecimal valorTotal = BigDecimal.ZERO;
        if (piso2 == null && !sillasPiso2.isEmpty())
            throw new ValidationException("pasajes", "Hay un numero dela silla invalida");

        if (!sillasPiso1.isEmpty()) {
            validarSillas(piso1, precio1, sillasPiso1);
            valorTotal = valorTotal.add(precio1.getPrecio().multiply(BigDecimal.valueOf(sillasPiso1.size())));
        }

        if (!sillasPiso2.isEmpty()) {
            validarSillas(piso2, precio2, sillasPiso2);
            valorTotal = valorTotal.add(precio2.getPrecio().multiply(BigDecimal.valueOf(sillasPiso2.size())));
        }

        if (valorTotal.compareTo(BigDecimal.ZERO) == 0)
            throw new ValidationException("pasajes", "La suma delos pasajes es zero");

        boolean enEfectivo = false;
        boolean estaPago = true;

        FaturaPasagemModel pago = facturaPasajeService.saveEmpresa(valorTotal, viaje, dto.metodo(), estaPago);

        viaje.addValorArrecadadoNoWeb(pago.getValorTotal());
        if (dto.metodo().equals(TipoPagamento.EFECTIVO)) {
            enEfectivo = true;
            viaje.addValorArrecadadoEfectivo(pago.getValorTotal());
        }

        if (!sillasPiso1.isEmpty()) {
            actualizarNSillasDisponibles(precio1, sillasPiso1);
            precioService.updateFromService(precio1);
        }

        if (precio2 != null && !sillasPiso2.isEmpty()) {
            actualizarNSillasDisponibles(precio2, sillasPiso2);
            precioService.updateFromService(precio2);
        }

        viajeRepository.save(viaje);//Actualizar los valores arrecadados

        List<PassagemModel> pasajes = new ArrayList<>();
        for (PasagemDTO pasagemDTO : sillasPiso1) {
            var pasaje = new PassagemModel(pasagemDTO, false, precio1.getPrecio(), estaPago, enEfectivo, salida, destino, precio1, pago);
            pasajes.add(pasaje);
        }

        if (precio2 != null) for (PasagemDTO pasagemDTO : sillasPiso2) {
            var pasaje = new PassagemModel(pasagemDTO, false, precio2.getPrecio(), estaPago, enEfectivo, salida, destino, precio2, pago);
            pasajes.add(pasaje);
        }

        pasajeRepository.saveAll(pasajes);
        return pago.getId();
    }

    /*public List<List<Integer>> dividirListas(PasajesDTOVenta dto, ViajeModel viaje) {
        LinkedList<Integer> pilha = new LinkedList<>();
        dto.pasajes().forEach(pasaje -> pilha.add(pasaje.nSilla()));
        int nPisos = viaje.getAutobus().getPisos().size();
        List<PisoModel> pisos = new ArrayList<>();

        List<List<Integer>> sillasOfPiso = new ArrayList<>();

        for (int i = 1; i <= nPisos; i++) {
            pisos.add(viaje.getAutobus().getPisoByNumero(i));
            sillasOfPiso.add(new ArrayList<>());
        }

        boolean hasInsert;
        while (!pilha.isEmpty()) {
            int nSilla = pilha.removeFirst();
            hasInsert = false;
            for (int i = 0; i < nPisos; i++) {
                if (pisos.get(i).hasNSilla(nSilla)) {
                    sillasOfPiso.get(0).add(nSilla);
                    hasInsert = true;
                }
            }
            if (!hasInsert) throw new ValidationException("pasajes", "Una delas sillas es invalido");
        }
        return sillasOfPiso;
    }*/

    public List<PasagemDTOEmpresaResponse> getPasajesFromPrecio(UUID idPrecio) {
        return pasajeRepository.findByPrecoIdAndEstaPago(idPrecio, true).stream().map(PasagemDTOEmpresaResponse::new).toList();
    }

    //Validadores
    public void actualizarNSillasDisponibles(PrecoModel precio, List<PasagemDTO> sillasPiso) {
        int nSillasDisponibles = precio.getNSillasDisponibles() - sillasPiso.size();
        if (nSillasDisponibles < 0)
            throw new ValidationException("pasajes", "No hay sillas disponibles");
        if (nSillasDisponibles == 0) precio.setLleno(true);
        precio.setNSillasDisponibles(nSillasDisponibles);
    }

    public void validarSillas(PisoModel piso, PrecoModel precio, List<PasagemDTO> sillasSolicitadas) {
        int numeroMinimo = piso.getPrimeraSilla();
        int numeroMaximo = piso.getUltimaSilla();

        List<Integer> ocupados = pasajeRepository.getPasajesVendidosAndNoRembolso(precio.getId());

        if (precio.getNSillasDisponibles() < sillasSolicitadas.size())
            throw new ValidationException("pasajes", "No hay tantas sillas disponibles");

        for (PasagemDTO sillasSolicitada : sillasSolicitadas) {
            for (Integer ocupado : ocupados)
                if (ocupado.equals(sillasSolicitada.numeroAssento()))
                    throw new ValidationException("El viaje ya posse un pasaje registrado");

            if (sillasSolicitada.numeroAssento() > numeroMaximo || sillasSolicitada.numeroAssento() < numeroMinimo)
                throw new ValidationException("nSilla", "El numero de Silla informado es invalido");
        }
    }

    public void delete(UUID idPasaje) {
        var pasajeModel = findById(idPasaje);

        if (!pasajeModel.getFaturaPasagem().getEstaPago()) {
            logger.error("Se intento reembolsar un pasaje no pagado");
            throw new RestRuntimeException("El pasaje no fue pagado");
        }

        if (pasajeModel.getFaturaReembolso() != null) throw new RestRuntimeException("El pasaje ya fue rembolsado");
        var viaje = pasajeModel.getPreco().getViagem();
        boolean resultado;
        if (pasajeModel.getEmDinheiro()) {
            resultado = viaje.substractValueEfectivo(pasajeModel.getPrecoPago());
        } else if (!pasajeModel.getCompradoWeb())
            resultado = viaje.substractValueNoWeb(pasajeModel.getPrecoPago());
        else {
            logger.warn("Se necessita una API para esta operacion");
            throw new RestRuntimeException("El pasaje fue comprado en la web, no esta disponible");
        }
        if (!resultado) {
            logger.warn("Se intento retirar un valor que no se debia del valor arrecadado");
            throw new RestRuntimeException("El valor arrecadado es menor que el de pasajes");
        }

        var facturaRembolsada = new FaturaReembolsoModel(pasajeModel.getPrecoPago(), pasajeModel.getFaturaPasagem(), pasajeModel);
        pasajeModel.setFaturaReembolso(facturaRembolsada);
        pasajeRepository.save(pasajeModel);
    }
}
