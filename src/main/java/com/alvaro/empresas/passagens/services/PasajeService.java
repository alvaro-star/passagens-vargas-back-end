package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajeDTO;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTO;
import com.alvaro.empresas.passagens.models.*;
import com.alvaro.empresas.passagens.repositories.PagoRepository;
import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import com.alvaro.empresas.passagens.repositories.SillaRepository;
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
    private SillaRepository sillaRepository;
    @Autowired
    private ViajeService viajeService;
    @Autowired
    private PagoRepository pagoRepository;

    public PasajeModel findById(UUID id) {
        var model = pasajeRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, PasajeModel.class.getName()));
    }

    public void getOne(UUID id) {
        var model = findById(id);
    }

    @Transactional
    public Object save(PasajesDTO dto) {
        var viaje = viajeService.findById(dto.idViaje());
        var trayecto = viaje.getTrayecto();
        Float precioTotal = 0.0f;

        //A gente so verifica nesta passagem, pois na salvacao vamos nos assegurar se ha outras pasagens
        for (SillaModel silla : viaje.getSillas()) {
            for (PasajeDTO pasajeDTO : dto.pasajes()) {
                if (silla.getNumero() == pasajeDTO.nSilla()) {
                    throw new ValidationException("El viaje ya posse un pasaje registrado");
                }
            }
        }

        //Verifica el numero de sillas del pasaje
        for (PasajeDTO pasajeDTO : dto.pasajes()) {
            var pasajeModel = new PasajeModel();
            pasajeModel.setNombre(pasajeDTO.nombre());
            pasajeModel.setCarnet(pasajeDTO.carnet());
            pasajeModel.setNascimento(pasajeDTO.nascimento());
            pasajeModel.setTrayecto(trayecto);
            pasajeModel.setCompradoWeb(true);

            var pasajeSaved = pasajeRepository.save(pasajeModel);

            Integer nPiso = validarSilla(trayecto, pasajeDTO.nSilla());

            sillaRepository.save(new SillaModel(pasajeDTO.nSilla(), nPiso, viaje, pasajeSaved));

            //Salvar a pasajem em outras listas
            var viajeDestino = viaje.getDestino().getDataHora();
            var viajeSalida = viaje.getSalida().getDataHora();
            List<ViajeModel> viajesEnComun = viajeService.findViajesBeteween(trayecto.getCodigo(), viajeSalida, viajeDestino);

            for (ViajeModel viajeModel : viajesEnComun) {
                if (viajeModel.getId() != viaje.getId()) {
                    sillaRepository.save(new SillaModel(pasajeDTO.nSilla(), nPiso, viaje));
                }
            }
            //Calculando a fatura
            for (PrecioModel precio : viaje.getPrecios()) {
                if (precio.getNPiso() == nPiso) {
                    precioTotal += precio.getPrecio();
                    break;
                }
            }
        }

        PagoModel pago = new PagoModel();
        pago.setValor(precioTotal);
        pago.setDescuento(dto.descuento());
        var tasa = precioTotal * 0.1f;
        pago.setTasaServicio(tasa);
        pago.setEstaPagado(true);
        pagoRepository.save(pago);
        //Pasaje salvado


        /************
         Falta o Pago, criar uma fatura
         boolean salvado = salvarSilla(trayecto, dto.nSilla(), nPiso, viaje, pasajeSaved);
         if (!salvado) {
         return false;
         }
         var viajeModelSalida = viajeModel.getSalida().getDataHora();
         var viajeModelDestino = viajeModel.getDestino().getDataHora();
         boolean juntos = viajeDestino.isAfter(viajeModelSalida) && viajeModelDestino.isAfter(viajeSalida);
         if (juntos) {
         sillaRepository.save(new SillaModel(nSilla, SillaNPiso, viaje));
         }
         ************/

        return true;
    }

    public Integer validarSilla(TrayectoModel trayecto, Integer nSilla) {
        List<PisoModel> pisos = trayecto.getAutobus().getPisos();
        if (pisos.size() == 1) {
            if (nSilla > pisos.get(0).getNSillas()) {
                throw new ValidationException("El numero dela silla es invalido");
            }
            return 1;
        } else {
            int indiceSegundoPiso = (pisos.get(0).getNPiso() == 2) ? 0 : 1;
            int nUltimaSilla = pisos.get(indiceSegundoPiso).getNSillas() + pisos.get(indiceSegundoPiso).getPrimeraSilla() - 1;


            if (nSilla > nUltimaSilla) {
                throw new ValidationException("El numero dela silla es invalido");
            }
            if (nSilla < pisos.get(indiceSegundoPiso).getPrimeraSilla()) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    public void salvarSilla(TrayectoModel trayecto, Integer nSilla, Integer SillaNPiso, ViajeModel viaje, PasajeModel pasajeSaved) {

    }
}
