package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.services.AutobusService;
import com.alvaro.empresas.passagens.dtos.TrayectoDto;
import com.alvaro.empresas.passagens.models.TrayectoModel;
import com.alvaro.empresas.passagens.repositories.TrayectoRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public TrayectoDto getOne(UUID id) {
        var model = this.findById(id);
        var dto = new TrayectoDto(model);
        dto.setIdAutobus(model.getAutobus().getId());
        return dto;
    }

    public List<TrayectoDto> getAll() {
        List<TrayectoModel> models = trayectoRepository.findAll();
        List<TrayectoDto> dtos = new ArrayList<>();

        models.forEach(model -> {
            var dto = new TrayectoDto(model);
            dto.setIdAutobus(model.getAutobus().getId());
            dtos.add(dto);
        });
        return dtos;
    }

    public TrayectoDto save(TrayectoDto dto) {
        var autobus = autobusService.findById(dto.getIdAutobus());
        var model = new TrayectoModel();
        model.setAutobus(autobus);

        var save = trayectoRepository.save(model);

        var saveDto = new TrayectoDto(save);
        saveDto.setIdAutobus(autobus.getId());
        return saveDto;
    }

    public TrayectoDto update(TrayectoDto dto, UUID id) {
        var autobus = autobusService.findById(dto.getIdAutobus());
        var model = this.findById(id);
        model.setAutobus(autobus);
        var update = trayectoRepository.save(model);
        var updateDto = new TrayectoDto(update);
        updateDto.setIdAutobus(autobus.getId());
        return updateDto;
    }

    public void delete(TrayectoModel model) {
        trayectoRepository.delete(model);
    }

}
