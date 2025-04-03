package com.alvaro.empresas.passagens.paradas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.alvaro.empresas.passagens.dtos.PageOutput;
import com.alvaro.empresas.passagens.paradas.dtos.LugarCreateDTO;
import com.alvaro.empresas.passagens.paradas.dtos.LugarUpdateDTO;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.repositories.CidadeRepository;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;

@Service
public class LugarService {
    @Autowired
    private LugarRepository lugarRepository;
    @Autowired
    private ParadaRepository paradaRepository;
    @Autowired
    private CidadeRepository cidadeRepository;

    public LugarModel findById(Integer id) {
        return lugarRepository.findByIdOrThr(id);
    }

    public PageOutput<LugarModel> findAll(Pageable pageable) {
        var page = lugarRepository.findAll(pageable);
        return new PageOutput<>(page);
    }

    public LugarModel save(LugarCreateDTO dto) {
        var cidade = cidadeRepository.findByIdOrThr(dto.idCidade());
        var model = new LugarModel(dto, cidade);
        return lugarRepository.save(model);
    }

    public LugarModel update(LugarUpdateDTO dto, Integer id) {
        var model = this.findById(id);
        model.setNome(dto.nome().toUpperCase());
        return lugarRepository.save(model);
    }

    public void delete(Integer id) {
        var model = lugarRepository.findByIdOrThr(id);
        var parada = paradaRepository.findFirst1ByLugarId(model.getId());
        if (parada.isPresent()) {
            model.setEnabled(false);
            lugarRepository.save(model);
        } else
            lugarRepository.delete(model);
    }

    public PageOutput<LugarModel> findByCidadeId(Integer id, Pageable pageable) {
        var page = lugarRepository.findByCidadeId(id, pageable);
        return new PageOutput<>(page);
    }
}