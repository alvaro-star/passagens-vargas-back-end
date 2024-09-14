package com.alvaro.empresas.passagens.paradas.services;

import com.alvaro.empresas.passagens.paradas.dtos.LugarDTO;
import com.alvaro.empresas.passagens.paradas.dtos.LugarDtoUpdate;
import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class LugarService {
    @Autowired
    private LugarRepository lugarRepository;

    @Autowired
    private ParadaRepository paradaRepository;

    public LugarModel findById(Integer id) {
        var model = lugarRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, LugarModel.class.getName()));
    }

    public List<LugarModel> findAllById(Set<Integer> ids){
        return lugarRepository.findAllById(ids);
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
        model.setNombre(dto.nombre().toUpperCase());
        return lugarRepository.save(model);
    }

    public void delete(LugarModel model) {
        var parada = paradaRepository.findFirst1ByLugarId(model.getId());
        if (parada.isPresent()) {
            model.setEnable(false);
            lugarRepository.save(model);
        } else {
            lugarRepository.delete(model);
        }
    }
}
