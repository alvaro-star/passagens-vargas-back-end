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

    public TrayectoDTOResponse getOne(UUID id) {
        var model = this.findById(id);
        var dto = new TrayectoDTOResponse(model);
        dto.setIdAutobus(model.getAutobus().getId());

        List<ParadaDTO> paradasDTOs = new ArrayList<>();
        for (ParadaModel paradaModel : model.getParadas()) {
            paradasDTOs.add(new ParadaDTO(paradaModel, paradaModel.getLugar().getId(), model.getCodigo()));
        }

        List<ViajeDTOList> viajesDTOs = new ArrayList<>();
        for (ViajeModel viajeModel : model.getViajes()) {
            viajesDTOs.add(new ViajeDTOList(viajeModel, model.getCodigo(), viajeModel.getSalida().getId(), viajeModel.getDestino().getId()));
        }

        dto.setParadas(paradasDTOs);
        dto.setViajes(viajesDTOs);

        return dto;
    }

    public List<TrayectoDTO> getAll() {
        List<TrayectoModel> models = trayectoRepository.findAll();
        List<TrayectoDTO> dtos = new ArrayList<>();

        models.forEach(model -> {
            var dto = new TrayectoDTO(model);
            dto.setIdAutobus(model.getAutobus().getId());
            dtos.add(dto);
        });
        return dtos;
    }

    public TrayectoDTO save(TrayectoDTO dto) {
        var autobus = autobusService.findById(dto.getIdAutobus());
        var model = new TrayectoModel();
        model.setAutobus(autobus);

        var save = trayectoRepository.save(model);

        var saveDto = new TrayectoDTO(save);
        saveDto.setIdAutobus(autobus.getId());
        return saveDto;
    }

    public TrayectoDTO update(TrayectoDTO dto, UUID id) {
        var autobus = autobusService.findById(dto.getIdAutobus());
        var model = this.findById(id);
        model.setAutobus(autobus);
        var update = trayectoRepository.save(model);
        var updateDto = new TrayectoDTO(update);
        updateDto.setIdAutobus(autobus.getId());
        return updateDto;
    }

    public void delete(TrayectoModel model) {
        trayectoRepository.delete(model);
    }

}
