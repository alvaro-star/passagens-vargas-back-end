package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.services.AutobusService;
import com.alvaro.empresas.passagens.dtos.TrayectoDTO;
import com.alvaro.empresas.passagens.dtos.TrayectoDTOResponse;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOList;
import com.alvaro.empresas.passagens.models.TrayectoModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.repositories.TrayectoRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TrayectoService {
    @Autowired
    private TrayectoRepository trayectoRepository;
    @Autowired
    private AutobusService autobusService;

    public TrayectoModel findById(UUID id) {
        var model = trayectoRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, TrayectoModel.class.getName()));
    }

    public TrayectoDTOResponse getOne(UUID id) {
        var model = this.findById(id);

        List<ParadaDTO> paradasDTOs = new ArrayList<>();
        for (ParadaModel paradaModel : model.getParadas()) {
            paradasDTOs.add(new ParadaDTO(paradaModel, paradaModel.getLugar().getId(), model.getCodigo()));
        }

        List<ViajeDTOList> viajesDTOs = new ArrayList<>();
        for (ViajeModel viajeModel : model.getViajes()) {
            viajesDTOs.add(new ViajeDTOList(viajeModel, model.getCodigo(), viajeModel.getSalida().getId(), viajeModel.getDestino().getId()));
        }

        return new TrayectoDTOResponse(model, model.getAutobus().getId(), paradasDTOs, viajesDTOs);
    }

    public Page<TrayectoDTO> getAll(Pageable pageable) {
        Page<TrayectoModel> models = trayectoRepository.findAll(pageable);
        return models.map(model -> new TrayectoDTO(model, model.getAutobus().getId()));
    }

    @Transactional
    public TrayectoDTO save(TrayectoDTO dto) {
        var autobus = autobusService.findById(dto.idAutobus());
        var model = new TrayectoModel();
        model.setAutobus(autobus);
        var save = trayectoRepository.save(model);
        return new TrayectoDTO(save, autobus.getId());
    }

    public TrayectoDTO update(TrayectoDTO dto, UUID id) {
        var autobus = autobusService.findById(dto.idAutobus());
        var model = this.findById(id);
        model.setAutobus(autobus);

        var update = trayectoRepository.save(model);
        return new TrayectoDTO(update, autobus.getId());
    }

    @Transactional
    public void delete(TrayectoModel model) {
        trayectoRepository.delete(model);
    }

}
