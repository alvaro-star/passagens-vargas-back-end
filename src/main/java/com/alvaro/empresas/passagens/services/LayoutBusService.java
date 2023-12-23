package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.dtos.AsientoBloqueadoDTO;
import com.alvaro.empresas.passagens.dtos.LayoutBusDTO;
import com.alvaro.empresas.passagens.enums.autobus.EnumPosicao;
import com.alvaro.empresas.passagens.enums.autobus.EnumTipoBus;
import com.alvaro.empresas.passagens.models.AsientoBloqueadoModel;
import com.alvaro.empresas.passagens.models.LayoutBusModel;
import com.alvaro.empresas.passagens.repositories.AsientoBloqueadoRepository;
import com.alvaro.empresas.passagens.repositories.LayoutBusRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LayoutBusService {
    @Autowired
    private LayoutBusRepository layoutBusRepository;
    @Autowired
    private AsientoBloqueadosService asientoBloqueadosService;
    @Autowired
    private AsientoBloqueadoRepository asientoBloqueadoRepository;

    public LayoutBusModel findById(Integer id) {
        Optional<LayoutBusModel> model = layoutBusRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, LayoutBusModel.class.getName()));
    }

    public List<LayoutBusModel> findAll() {
        return layoutBusRepository.findAll();
    }

    @Transactional
    public LayoutBusModel save(LayoutBusDTO dto) {

        var layoutModel = new LayoutBusModel();
        layoutModel.llenarSinVector(dto);
        var layoutModelSave = layoutBusRepository.save(layoutModel);
        asientoBloqueadosService.saveAll(dto.getAsientosBloqueados(), layoutModelSave);

        return layoutModelSave;
    }

    @Transactional
    public LayoutBusModel update(LayoutBusDTO dto, Integer id) {
        var model = this.findById(id);
        model.llenarSinVector(dto);

        asientoBloqueadosService.deleteAll(model.getAsientosBloqueados());
        model.setAsientosBloqueados(new ArrayList<AsientoBloqueadoModel>());

        var modelUpdate = layoutBusRepository.save(model);
        List<AsientoBloqueadoModel> asientoBloqueadoModels = asientoBloqueadosService.saveAll(dto.getAsientosBloqueados(), modelUpdate);
        modelUpdate.setAsientosBloqueados(asientoBloqueadoModels);
        return modelUpdate;
    }

    public void delete(Integer id) {
        var model = this.findById(id);
        asientoBloqueadosService.deleteAll(model.getAsientosBloqueados());
        layoutBusRepository.delete(model);
    }
}
