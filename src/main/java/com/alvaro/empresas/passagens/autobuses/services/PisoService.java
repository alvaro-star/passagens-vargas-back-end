package com.alvaro.empresas.passagens.autobuses.services;

import com.alvaro.empresas.passagens.autobuses.dtos.PisoDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.PisoDtoUpdate;
import com.alvaro.empresas.passagens.autobuses.models.AsientoBloqueadoModel;
import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.repositories.AsientoBloqueadoRepository;
import com.alvaro.empresas.passagens.autobuses.repositories.PisoRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PisoService {
    @Autowired
    private PisoRepository pisoRepository;
    @Autowired
    private AsientoBloqueadosService asientoBloqueadosService;
    @Autowired
    private AsientoBloqueadoRepository asientoBloqueadoRepository;

    public PisoModel findById(Integer id) {
        Optional<PisoModel> model = pisoRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, PisoModel.class.getName()));
    }

    public List<PisoModel> findAll() {
        return pisoRepository.findAll();
    }

    @Transactional
    public PisoModel save(PisoDTO dto, AutobusModel autobus) {
        var layoutModel = new PisoModel(dto);
        layoutModel.setAutobus(autobus);
        var layoutModelSave = pisoRepository.save(layoutModel);
        asientoBloqueadosService.saveAll(dto.getAsientosBloqueados(), layoutModelSave);

        return layoutModelSave;
    }

    @Transactional
    public PisoModel update(PisoDtoUpdate dto, Integer id) {
        var model = this.findById(id);
        model.llenarSinVector(dto);

        asientoBloqueadosService.deleteAll(model.getAsientosBloqueados());
        model.setAsientosBloqueados(new ArrayList<AsientoBloqueadoModel>());

        var modelUpdate = pisoRepository.save(model);
        List<AsientoBloqueadoModel> asientoBloqueadoModels = asientoBloqueadosService.saveAll(dto.getAsientosBloqueados(), modelUpdate);
        modelUpdate.setAsientosBloqueados(asientoBloqueadoModels);
        return modelUpdate;
    }

    public void delete(Integer id) {
        var model = this.findById(id);
        //Este Metodo devido ao efeito cascada elimina os assientos bloqueados
        pisoRepository.delete(model);
    }
}
