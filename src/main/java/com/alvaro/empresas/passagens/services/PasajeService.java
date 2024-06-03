package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajeDTO;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTO;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTOVenta;
import com.alvaro.empresas.passagens.enums.MetodoPagamentoEnum;
import com.alvaro.empresas.passagens.models.*;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PasajeService {
    private final PasajeRepository pasajeRepository;
    private final PrecioService precioService;
    private final PagoService pagoService;
    private final ParadaRepository paradaRepository;
    private final ViajeService viajeService;
    private final ViajeRepository viajeRepository;

    @Autowired
    public PasajeService(PasajeRepository pasajeRepository, PrecioService precioService, PagoService pagoService, ParadaRepository paradaRepository, ViajeService viajeService, ViajeRepository viajeRepository) {
        this.pasajeRepository = pasajeRepository;
        this.precioService = precioService;
        this.pagoService = pagoService;
        this.paradaRepository = paradaRepository;
        this.viajeService = viajeService;
        this.viajeRepository = viajeRepository;
    }

    public PasajeModel findById(UUID id) {
        var model = pasajeRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, PasajeModel.class.getName()));
    }

    //En desarrollo
    public void getOne(UUID id) {
        var model = findById(id);
    }

    //Solo podra salvar pasajes de un mismo piso
    @Transactional
    public PagoModel save(PasajesDTO dto, MetodoPagamentoEnum metodo, boolean guardarContacto, boolean compradoWeb) {
        var precio = precioService.findById(dto.idPrecio());
        var viaje = precio.getViaje();
        ParadaModel salida;
        ParadaModel destino;

        List<Integer> ocupados = pasajeRepository.getPasajesVendidos(precio.getId());

        validarSilla(viaje, precio, dto.pasajes(), ocupados);

        salida = viaje.getParadaByLugarId(dto.idLugarSalida());

        if (salida == null)
            throw new ValidationException(new FieldMessage("idLugarSalida", "La salida no hace parte del trayecto"));

        destino = viaje.getParadaByLugarId(dto.idLugarDestino());

        if (destino == null)
            throw new ValidationException(new FieldMessage("idLugarDestino", "El destino no hace parte del trayecto"));

        boolean estaPago;

        BigDecimal valorTotal = precio.getPrecio().multiply(BigDecimal.valueOf(dto.pasajes().size()));
        PagoModel pago = pagoService.save(dto.contacto(), valorTotal, viaje, metodo, guardarContacto);

        switch (metodo) {
            case QR -> estaPago = false;
            case EFECTIVO -> {
                estaPago = true;
                BigDecimal valorArrecadado = viaje.getValorArrecadadoEfectivo() != null ? viaje.getValorArrecadadoEfectivo() : BigDecimal.ZERO;
                BigDecimal valorTotalPago = pago.getValorTotal() != null ? pago.getValorTotal() : BigDecimal.ZERO;
                viaje.setValorArrecadadoEfectivo(valorArrecadado.add(valorTotalPago));
                viajeRepository.save(viaje);
            }
            default -> throw new ValidationException(new FieldMessage("metodo", "Metodo de Pago invalido"));
        }

        for (PasajeDTO pasajeDTO : dto.pasajes()) {
            var pasajero = new PasajeroModel(pasajeDTO);
            var pasajeModel = new PasajeModel(pasajeDTO.nSilla(), compradoWeb, estaPago, salida, destino, precio, pago, pasajero);
            pasajeRepository.save(pasajeModel);
        }

        if (metodo == MetodoPagamentoEnum.EFECTIVO) {
            int nSillasDisponibles = precio.getNSillasDisponibles() - dto.pasajes().size();

            if (nSillasDisponibles < 0)
                throw new ValidationException(new FieldMessage("pasajes", "No hay sillas disponibles"));

            precio.setNSillasDisponibles(nSillasDisponibles);

            if (nSillasDisponibles == 0)
                precio.setLleno(true);

            precioService.updateFromService(precio);
        }

        return pago;
    }

    @Transactional
    public PagoModel saveEmpresa(PasajesDTOVenta dto, MetodoPagamentoEnum metodo, boolean guardarContacto, boolean compradoWeb) {
        var viaje = viajeService.findById(dto.idViaje());
        ParadaModel salida;
        ParadaModel destino;
        List<ParadaModel> salidas = paradaRepository.findByViajeCodigoAndLugarId(viaje.getCodigo(), dto.idLugarSalida());
        List<ParadaModel> destinos = paradaRepository.findByViajeCodigoAndLugarId(viaje.getCodigo(), dto.idLugarSalida());
        if (salidas.size() != 1)
            throw new ValidationException("idLugarSalida", "Ocurrio un error con la parada de salida");
        if (destinos.size() != 1)
            throw new ValidationException("idLugarDestino", "Ocurrio un error con la parada de destino");

        salida = salidas.get(0);
        destino = destinos.get(0);


        List<PasajeDTO> sillasPiso1 = new ArrayList<>();
        List<PasajeDTO> sillasPiso2 = new ArrayList<>();
        PisoModel piso1 = viaje.getAutobus().getPisoByNumero(1);
        PisoModel piso2 = viaje.getAutobus().getPisoByNumero(2);

        for (PasajeDTO pasajeFor : dto.pasajes()) {
            if (pasajeFor.nSilla() > 0 && pasajeFor.nSilla() <= piso1.getNSillas())
                sillasPiso1.add(pasajeFor);
            else
                sillasPiso2.add(pasajeFor);
        }

        PrecioModel precio1 = viaje.getPrecioByNPiso(1);
        PrecioModel precio2 = viaje.getPrecioByNPiso(2);

        BigDecimal valorTotal = new BigDecimal("0");
        if (piso2 == null) {
            if (!sillasPiso2.isEmpty())
                throw new ValidationException("pasajes", "Hay un numero dela silla invalida");
            validarSillasEmpresa(piso1, precio1, sillasPiso1);
            valorTotal = precio1.getPrecio().multiply(BigDecimal.valueOf(sillasPiso1.size()));
        } else {
            if (!sillasPiso2.isEmpty()) {
                validarSillasEmpresa(piso2, precio2, sillasPiso2);
                valorTotal = valorTotal.add(precio2.getPrecio().multiply(BigDecimal.valueOf(sillasPiso2.size())));
            }
            if (!sillasPiso1.isEmpty()) {
                validarSillasEmpresa(piso1, precio1, sillasPiso1);
                valorTotal = valorTotal.add(precio1.getPrecio().multiply(BigDecimal.valueOf(sillasPiso1.size())));
            }
        }

        if (valorTotal.compareTo(BigDecimal.ZERO) == 0)
            throw new ValidationException("pasajes", "La suma delos pasajes es zero");

        boolean estaPago;

        PagoModel pago = pagoService.save(dto.contacto(), valorTotal, viaje, metodo, guardarContacto);

        switch (metodo) {
            case QR -> estaPago = false;
            case EFECTIVO -> {
                estaPago = true;
                BigDecimal valorArrecadado = viaje.getValorArrecadadoEfectivo() != null ? viaje.getValorArrecadadoEfectivo() : BigDecimal.ZERO;
                BigDecimal valorTotalPago = pago.getValorTotal() != null ? pago.getValorTotal() : BigDecimal.ZERO;
                viaje.setValorArrecadadoEfectivo(valorArrecadado.add(valorTotalPago));
                viajeRepository.save(viaje);
            }
            default -> throw new ValidationException(new FieldMessage("metodo", "Metodo de Pago invalido"));
        }


        if (metodo == MetodoPagamentoEnum.EFECTIVO) {
            if (!sillasPiso1.isEmpty()) {
                int nSillasDisponibles = precio1.getNSillasDisponibles() - sillasPiso1.size();
                if (nSillasDisponibles < 0)
                    throw new ValidationException(new FieldMessage("pasajes", "No hay sillas disponibles"));
                precio1.setNSillasDisponibles(nSillasDisponibles);
                if (nSillasDisponibles == 0)
                    precio1.setLleno(true);
                precioService.updateFromService(precio1);
            }

            if (precio2 != null && !sillasPiso2.isEmpty()) {
                int nSillasDisponibles = precio2.getNSillasDisponibles() - sillasPiso2.size();
                if (nSillasDisponibles < 0)
                    throw new ValidationException(new FieldMessage("pasajes", "No hay sillas disponibles"));
                precio1.setNSillasDisponibles(nSillasDisponibles);
                if (nSillasDisponibles == 0)
                    precio1.setLleno(true);
                precioService.updateFromService(precio1);
            }
        }

        for (PasajeDTO pasajeDTO : sillasPiso1) {
            var pasajero = new PasajeroModel(pasajeDTO);
            var pasajeModel = new PasajeModel(pasajeDTO.nSilla(), compradoWeb, estaPago, salida, destino, precio1, pago, pasajero);
            pasajeRepository.save(pasajeModel);
        }
        if (precio2 != null) {
            for (PasajeDTO pasajeDTO : sillasPiso2) {
                var pasajero = new PasajeroModel(pasajeDTO);
                var pasajeModel = new PasajeModel(pasajeDTO.nSilla(), compradoWeb, estaPago, salida, destino, precio2, pago, pasajero);
                pasajeRepository.save(pasajeModel);
            }
        }
        return pago;
    }

    //Validadores

    public void validarSillasEmpresa(PisoModel piso, PrecioModel precio, List<PasajeDTO> sillasSolicitadas) {
        int numeroMinimo = piso.getPrimeraSilla();
        int numeroMaximo = piso.getNSillas() + piso.getPrimeraSilla() - 1;

        List<Integer> ocupados = pasajeRepository.getPasajesVendidos(precio.getId());

        if (precio.getNSillasDisponibles() < sillasSolicitadas.size())
            throw new ValidationException(new FieldMessage("pasajes", "No hay tantas sillas disponibles"));

        for (PasajeDTO sillasSolicitada : sillasSolicitadas) {
            for (Integer ocupado : ocupados)
                if (ocupado.equals(sillasSolicitada.nSilla()))//Erro
                    throw new ValidationException("El viaje ya posse un pasaje registrado");

            if (sillasSolicitada.nSilla() > numeroMaximo || sillasSolicitada.nSilla() < numeroMinimo)
                throw new ValidationException(new FieldMessage("nSilla", "El numero de Silla informado es invalido"));
        }
    }

    public void validarSilla(ViajeModel viaje, PrecioModel precio, List<PasajeDTO> pasajesDTO, List<Integer> ocupados) {
        PisoModel piso = viaje.getAutobus().getPisoByNumero(precio.getNPiso());
        if (piso == null)
            throw new ValidationException(new FieldMessage("piso", "El piso informado no existe"));

        int numeroMinimo = piso.getPrimeraSilla();
        int numeroMaximo = piso.getNSillas() + piso.getPrimeraSilla() - 1;
        if (precio.getNSillasDisponibles() < pasajesDTO.size())
            throw new ValidationException(new FieldMessage("pasajes", "No hay tantas sillas disponibles"));

        for (PasajeDTO pasajeDTO : pasajesDTO) {
            for (Integer ocupado : ocupados)
                if (ocupado.equals(pasajeDTO.nSilla()))//Erro
                    throw new ValidationException("El viaje ya posse un pasaje registrado");

            if (pasajeDTO.nSilla() > numeroMaximo || pasajeDTO.nSilla() < numeroMinimo)
                throw new ValidationException(new FieldMessage("nSilla", "El numero de Silla informado es invalido"));
        }
    }
}
