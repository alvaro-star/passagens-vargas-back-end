package com.alvaro.empresas.passagens.paradas.services;

import com.alvaro.empresas.passagens.paradas.dtos.LugarDTO;
import com.alvaro.empresas.passagens.paradas.dtos.LugarDtoUpdate;
import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LugarService {
    @Autowired
    private LugarRepository lugarRepository;

    public LugarModel findById(Integer id) {
        var model = lugarRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, LugarModel.class.getName()));
    }

    public Page<LugarDTO> findAll(Pageable pageable) {
        Page<LugarModel> models = lugarRepository.findAll(pageable);
        return models.map(model -> new LugarDTO(model, model.getCiudad().getId()));
    }

    public LugarModel save(LugarDTO dto, CiudadModel ciudad) {
        var model = new LugarModel(dto);
        model.setCiudad(ciudad);
        return lugarRepository.save(model);
    }

    public LugarModel update(LugarDtoUpdate dto, Integer id) {
        var model = this.findById(id);
        model.setNombre(dto.getNombre());
        return lugarRepository.save(model);
    }

    public void delete(LugarModel model) {
        lugarRepository.delete(model);
    }
}
