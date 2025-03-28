package com.alvaro.empresas.passagens.paradas.services;

import com.alvaro.empresas.passagens.paradas.dtos.LugarDTO;
import com.alvaro.empresas.passagens.paradas.dtos.LugarDTOUpdate;
import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.EntityNotFoundException;
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
        return model.orElseThrow(() -> new EntityNotFoundException(id, LugarModel.class));
    }

    public List<LugarModel> findAllById(Set<Integer> ids) {
        return lugarRepository.findAllById(ids);
    }

    public Page<LugarDTO> findAll(Pageable pageable) {
        Page<LugarModel> models = lugarRepository.findAll(pageable);
        return models.map(LugarDTO::new);
    }

    public LugarModel save(LugarDTO dto, CidadeModel cidade) {
        var model = new LugarModel(dto, cidade);
        return lugarRepository.save(model);
    }

    public LugarModel update(LugarDTOUpdate dto, Integer id) {
        var model = this.findById(id);
        model.setNome(dto.nome().toUpperCase());
        return lugarRepository.save(model);
    }

    public void delete(LugarModel model) {
        var parada = paradaRepository.findFirst1ByLugarId(model.getId());
        if (parada.isPresent()) {
            model.setHabilitado(false);
            lugarRepository.save(model);
        } else
            lugarRepository.delete(model);
    }
}