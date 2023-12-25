package com.alvaro.empresas.passagens.autobuses.services;

import com.alvaro.empresas.passagens.autobuses.dtos.AsientoBloqueadoDTO;
import com.alvaro.empresas.passagens.autobuses.models.AsientoBloqueadoModel;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.repositories.AsientoBloqueadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AsientoBloqueadosService {
    @Autowired
    private AsientoBloqueadoRepository asientoBloqueadoRepository;

    public List<AsientoBloqueadoModel> saveAll(List<AsientoBloqueadoDTO> dtos, PisoModel layout) {
        List<AsientoBloqueadoModel> models = new ArrayList<>();
        dtos.forEach(dto -> {
            var model = new AsientoBloqueadoModel(dto);
            model.setPiso(layout);
            models.add(asientoBloqueadoRepository.save(model));
        });

        return models;
    }

    public void deleteAll(List<AsientoBloqueadoModel> list) {
        List<Integer> ids = new ArrayList<>();
        list.forEach(model -> {
            ids.add(model.getId());
        });
        ids.forEach(id -> {
            var model = asientoBloqueadoRepository.findById(id).get();
            asientoBloqueadoRepository.delete(model);
        });
    }

    public List<AsientoBloqueadoDTO> convertModelsToDtos(List<AsientoBloqueadoModel> models) {
        List<AsientoBloqueadoDTO> dtos = new ArrayList<>();
        models.forEach(model -> {
            dtos.add(new AsientoBloqueadoDTO(model));
        });
        return dtos;
    }
}
