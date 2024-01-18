package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.models.PosicionIndisponibleModel;
import com.alvaro.empresas.passagens.dtos.precios.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.precios.PrecioDTOResponseViaje;
import com.alvaro.empresas.passagens.dtos.precios.PrecioDTOUpdate;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import com.alvaro.empresas.passagens.repositories.PrecioRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PrecioService {
    @Autowired
    private PrecioRepository precioRepository;
    @Autowired
    private PasajeRepository pasajeRepository;

    public PrecioModel findById(UUID id) {
        var model = precioRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, PrecioModel.class.getName()));
    }

    public List<PrecioDTO> saveAll(List<PrecioModel> dtoModels, ViajeModel viaje) {
        List<PrecioDTO> salvos = new ArrayList<>();
        for (PrecioModel precioModel : dtoModels) {
            precioModel.setViaje(viaje);
            var save = precioRepository.save(precioModel);
            salvos.add(new PrecioDTO(save, viaje.getId()));
        }
        return salvos;
    }

    public PrecioDTO getOne(UUID id) {
        var model = findById(id);
        return new PrecioDTO(model, model.getViaje().getId());
    }

    public PrecioDTOResponseViaje vender(UUID id) {
        var model = findById(id);
        List<PisoModel> pisos = model.getViaje().getTrayecto().getAutobus().getPisos();
        var pisoElegido = new PisoModel();
        for (PisoModel piso : pisos) {
            if (piso.getNPiso() == model.getNPiso()) {
                pisoElegido = piso;
            }
        }

        List<Integer> bloqueados = new ArrayList<>();
        for (PosicionIndisponibleModel posicionesIndisponible : pisoElegido.getPosicionesIndisponibles()) {
            bloqueados.add(posicionesIndisponible.getNumero());
        }

        PisoDTOResponse pisoDto = new PisoDTOResponse(pisoElegido, model.getViaje().getTrayecto().getAutobus().getId(), bloqueados);

        List<Integer> ocupados = pasajeRepository.getPasajesVendidos(model.getId());
        return new PrecioDTOResponseViaje(model, pisoDto, ocupados);
    }

    public PrecioDTO update(PrecioDTOUpdate dto, UUID id) {
        var model = findById(id);
        model.updateValues(dto);
        var update = precioRepository.save(model);
        return new PrecioDTO(update, model.getViaje().getId());
    }

    public PrecioModel updateFromService(PrecioModel precioModel) {
        return precioRepository.save(precioModel);
    }
}
