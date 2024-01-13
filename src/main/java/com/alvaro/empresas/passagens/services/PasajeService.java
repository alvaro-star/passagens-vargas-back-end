package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajeDTO;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTO;
import com.alvaro.empresas.passagens.models.*;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import com.alvaro.empresas.passagens.repositories.PasajeroRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PasajeService {
    @Autowired
    private PasajeRepository pasajeRepository;
    @Autowired
    private PrecioService precioService;
    @Autowired
    private PagoService pagoService;
    @Autowired
    private PasajeroRepository pasajeroRepository;

    public PasajeModel findById(UUID id) {
        var model = pasajeRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, PasajeModel.class.getName()));
    }

    public void getOne(UUID id) {
        var model = findById(id);
    }

    //Solo podra salvar pasajes de un mismo piso
    @Transactional
    public PagoModel save(PasajesDTO dto) {
        var precio = precioService.findById(dto.idPrecio());
        var trayecto = precio.getViaje().getTrayecto();
        ParadaModel salida = null;
        ParadaModel destino = null;

        //Lancara excecoes se algum estiver invalido
        validarSilla(trayecto, precio, dto.pasajes());

        salida = trayecto.getParadaByLugarId(dto.idLugarSalida());

        if (salida == null) {
            throw new ValidationException(new FieldMessage("idLugarSalida", "La salida no hace parte del trayecto"));
        }

        destino = trayecto.getParadaByLugarId(dto.idLugarDestino());

        if (destino == null) {
            throw new ValidationException(new FieldMessage("idLugarDestino", "El destino no hace parte del trayecto"));
        }

        PagoModel pago = pagoService.save(dto, precio.getPrecio());

        for (PasajeDTO pasajeDTO : dto.pasajes()) {
            var pasajeModel = new PasajeModel();

            pasajeModel.setCompradoWeb(true);
            pasajeModel.setEstaPagado(false);
            pasajeModel.setNumero(pasajeDTO.nSilla());

            pasajeModel.setSalida(salida);
            pasajeModel.setDestino(destino);
            pasajeModel.setTrayecto(trayecto);
            pasajeModel.setDestino(destino);
            pasajeModel.setPrecio(precio);
            pasajeModel.setPago(pago);

            var pasajeSaved = pasajeRepository.save(pasajeModel);

            var pasajero = new PasajeroModel(pasajeDTO);
            pasajero.setPasaje(pasajeSaved);
            var pasajeroSaved = pasajeroRepository.save(pasajero);
        }
        precioService.updateFromService(precio);
        return pago;
    }

    public void validarSilla(TrayectoModel trayecto, PrecioModel precio, List<PasajeDTO> pasajesDTO) {
        List<PisoModel> pisos = trayecto.getAutobus().getPisos();
        int nPisos = pisos.size();
        int numeroMaximo = 0;
        int numeroMinimo = 0;
        if (precio.getNSillasDisponibles() < pasajesDTO.size()) {
            throw new ValidationException(new FieldMessage("pasajes", "No hay tantas sillas disponibles"));
        }

        switch (nPisos) {
            case 1:
                numeroMaximo = pisos.get(0).getNSillas();
                numeroMinimo = 1;
                break;
            case 2:
                PisoModel pisoElegido;
                if (pisos.get(0).getNPiso().equals(precio.getNPiso())) {
                    pisoElegido = pisos.get(0);
                } else {
                    pisoElegido = pisos.get(1);
                }
                numeroMaximo = pisoElegido.getNSillas() + pisoElegido.getPrimeraSilla() - 1;
                numeroMinimo = pisoElegido.getPrimeraSilla();
                break;
        }

        for (PasajeDTO pasajeDTO : pasajesDTO) {
            for (PasajeModel pasajeModel : precio.getPasajes()) {
                if (pasajeModel.getNumero().equals(pasajeDTO.nSilla())) {//Erro
                    throw new ValidationException("El viaje ya posse un pasaje registrado");
                }
            }
            if (pasajeDTO.nSilla() < numeroMinimo && pasajeDTO.nSilla() > numeroMaximo) {
                throw new ValidationException(new FieldMessage("nSilla", "El numero de Silla informado es invalido"));
            }
        }
    }
}
