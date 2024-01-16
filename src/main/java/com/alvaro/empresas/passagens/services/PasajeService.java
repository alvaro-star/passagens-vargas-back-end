package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.pasajes.ContactoDTO;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajeDTO;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTO;
import com.alvaro.empresas.passagens.enums.MetodoPagamentoEnum;
import com.alvaro.empresas.passagens.models.*;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import com.alvaro.empresas.passagens.repositories.PasajeroRepository;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
    @Autowired
    private UsuarioRepository usuarioRepository;

    public PasajeModel findById(UUID id) {
        var model = pasajeRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, PasajeModel.class.getName()));
    }

    public void getOne(UUID id) {
        var model = findById(id);
    }

    //Solo podra salvar pasajes de un mismo piso
    @Transactional
    public PagoModel save(PasajesDTO dto, MetodoPagamentoEnum metodo, boolean guardarContacto, boolean compradoWeb) {
        var precio = precioService.findById(dto.idPrecio());
        var trayecto = precio.getViaje().getTrayecto();
        ParadaModel salida;
        ParadaModel destino;

        validarSilla(trayecto, precio, dto.pasajes());

        salida = trayecto.getParadaByLugarId(dto.idLugarSalida());

        if (salida == null) {
            throw new ValidationException(new FieldMessage("idLugarSalida", "La salida no hace parte del trayecto"));
        }

        destino = trayecto.getParadaByLugarId(dto.idLugarDestino());

        if (destino == null) {
            throw new ValidationException(new FieldMessage("idLugarDestino", "El destino no hace parte del trayecto"));
        }

        PagoModel pago = pagoService.save(dto, precio.getPrecio(), metodo, guardarContacto);

        boolean estaPago;

        switch (metodo) {
            case QR -> estaPago = false;
            case EFECTIVO -> estaPago = true;
            default -> throw new ValidationException(new FieldMessage("metodo", "Metodo de Pago invalido"));
        }

        for (PasajeDTO pasajeDTO : dto.pasajes()) {
            var pasajeModel = new PasajeModel(pasajeDTO.nSilla(), compradoWeb, estaPago,
                    salida, destino, trayecto, precio, pago);

            var pasajeSaved = pasajeRepository.save(pasajeModel);

            var pasajero = new PasajeroModel(pasajeDTO);

            pasajero.setPasaje(pasajeSaved);
            pasajeroRepository.save(pasajero);
        }


        if (metodo == MetodoPagamentoEnum.EFECTIVO) {
            int nSillasDisponibles = precio.getNSillasDisponibles() - dto.pasajes().size();
            if (nSillasDisponibles == 0) {
                precio.setNSillasDisponibles(0);
                precio.setLleno(true);
            } else if (nSillasDisponibles > 0) {
                precio.setNSillasDisponibles(nSillasDisponibles);
            } else {
                throw new ValidationException(new FieldMessage("pasajes", "No hay sillas disponibles"));
            }
            precioService.updateFromService(precio);
        }

        return pago;
    }

    public void validarSilla(TrayectoModel trayecto, PrecioModel precio, List<PasajeDTO> pasajesDTO) {
        List<PisoModel> pisos = trayecto.getAutobus().getPisos();
        int nPisos = pisos.size();
        int numeroMaximo = 0;
        int numeroMinimo = 0;
        if (precio.getNSillasDisponibles() < pasajesDTO.size())
            throw new ValidationException(new FieldMessage("pasajes", "No hay tantas sillas disponibles"));

        switch (nPisos) {
            case 1 -> {
                numeroMaximo = pisos.get(0).getNSillas();
                numeroMinimo = 1;
            }
            case 2 -> {
                PisoModel pisoElegido;
                if (pisos.get(0).getNPiso().equals(precio.getNPiso())) {
                    pisoElegido = pisos.get(0);
                } else {
                    pisoElegido = pisos.get(1);
                }
                numeroMaximo = pisoElegido.getNSillas() + pisoElegido.getPrimeraSilla() - 1;
                numeroMinimo = pisoElegido.getPrimeraSilla();
            }
        }

        for (PasajeDTO pasajeDTO : pasajesDTO) {
            for (PasajeModel pasajeModel : precio.getPasajes()) {
                if (pasajeModel.getNSilla().equals(pasajeDTO.nSilla())) {//Erro
                    throw new ValidationException("El viaje ya posse un pasaje registrado");
                }
            }
            if (pasajeDTO.nSilla() < numeroMinimo && pasajeDTO.nSilla() > numeroMaximo) {
                throw new ValidationException(new FieldMessage("nSilla", "El numero de Silla informado es invalido"));
            }
        }
    }

    public boolean empresaValida(Authentication usuario, UUID idPrecio) {
        var autorizado = usuarioRepository.findByEmail(usuario.getName());
        var precio = precioService.findById(idPrecio);
        if (autorizado.getIdEmpresa() == precio.getViaje().getTrayecto().getAutobus().getEmpresa().getId()) {
            return true;
        }
        return false;
    }
}
