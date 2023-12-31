package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.dtos.PasajeDTO;
import com.alvaro.empresas.passagens.models.*;
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

    public PasajeModel findById(UUID id) {
        var model = pasajeRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, PasajeModel.class.getName()));
    }

    public void getOne(UUID id) {
        var model = findById(id);
    }

    @Transactional
    public Object save(PasajeDTO dto) {
        var viaje = viajeService.findById(dto.idViaje());
        var trayecto = viaje.getTrayecto();
        //A gente so verifica nesta passagem, pois na salvacao vamos nos assegurar se ha outras pasagens
        for (SillaModel silla : viaje.getSillas()) {
            if (silla.getNumero() == dto.nSilla()) {
                return false;
            }
        }

        var pasajeModel = new PasajeModel(dto);
        pasajeModel.setTrayecto(trayecto);
        var pasajeSaved = pasajeRepository.save(pasajeModel);
        //Pasaje salvado
        Integer nPiso = calcularNPiso(trayecto, dto.nSilla());
        if (nPiso == null) {
            return false;
        }

        boolean salvado = salvarSilla(trayecto, dto.nSilla(), nPiso, viaje, pasajeSaved);
        if (!salvado) {
            return false;
        }

        /************
         Falta o Pago
         ************/
        for (PrecioModel precioModel : viaje.getPrecios()) {
        }

        return null;
    }

    public Integer calcularNPiso(TrayectoModel trayecto, Integer nSilla) {
        List<PisoModel> pisos = trayecto.getAutobus().getPisos();
        if (pisos.size() == 1) {
            if (nSilla > pisos.get(0).getNSillas()) {
                return null;
            }
            return 1;
        } else {
            int indiceSegundoPiso = (pisos.get(0).getNPiso() == 2) ? 0 : 1;
            int nUltimaSilla = pisos.get(indiceSegundoPiso).getNSillas() + pisos.get(indiceSegundoPiso).getPrimeraSilla() - 1;

            if (nSilla > nUltimaSilla) {
                return null;
            }
            if (nSilla < pisos.get(indiceSegundoPiso).getPrimeraSilla()) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    public boolean salvarSilla(TrayectoModel trayecto, Integer nSilla, Integer SillaNPiso, ViajeModel viaje, PasajeModel pasajeSaved) {
        List<PisoModel> pisos = trayecto.getAutobus().getPisos();

        sillaRepository.save(new SillaModel(nSilla, SillaNPiso, viaje, pasajeSaved));

        var viajeDestino = viaje.getDestino().getDataHora();
        var viajeSalida = viaje.getSalida().getDataHora();

        for (ViajeModel viajeModel : trayecto.getViajes()) {
            if (viajeModel.getId() != viaje.getId()) {
                var viajeModelSalida = viajeModel.getSalida().getDataHora();
                var viajeModelDestino = viajeModel.getDestino().getDataHora();

                boolean juntos = viajeDestino.isAfter(viajeModelSalida) && viajeModelDestino.isAfter(viajeSalida);
                if (juntos) {
                    sillaRepository.save(new SillaModel(nSilla, SillaNPiso, viaje));
                }
            }
        }
        return true;
    }
}
