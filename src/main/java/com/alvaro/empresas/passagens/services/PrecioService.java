package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.dtos.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.PrecioDTOUpdate;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
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

    //No se podran salvar precios por separado
    /*
    public PrecioDTO save(PrecioDTO dto) {
        var viaje = viajeService.findById(dto.idViaje());
        //Maximo 2
        int nPisos = viaje.getTrayecto().getAutobus().getPisos().size();
        int nPrecios = viaje.getPrecios().size();

        viaje.setPrecios(new ArrayList<PrecioModel>());
        if (nPisos > nPrecios) {
            var model = new PrecioModel(dto.precio(), 2, viaje);
            var save = precioRepository.save(model);
            return new PrecioDTO(save, viaje.getId());
        }
        return null;
    }*/

    public PrecioDTO update(PrecioDTOUpdate dto, UUID id) {
        var model = findById(id);
        model.updateValues(dto);
        var update = precioRepository.save(model);
        return new PrecioDTO(update, model.getViaje().getId());
    }
}
