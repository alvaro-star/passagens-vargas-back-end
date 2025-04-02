package com.alvaro.empresas.passagens.paradas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.dtos.PageOutput;
import com.alvaro.empresas.passagens.paradas.dtos.CidadeCreateDTO;
import com.alvaro.empresas.passagens.paradas.dtos.CidadeUpdateDTO;
import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import com.alvaro.empresas.passagens.paradas.repositories.CidadeRepository;
import com.alvaro.empresas.passagens.paradas.repositories.DepartamentoRepository;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;

@Service
public class CidadeService {
    @Autowired
    private LugarRepository lugarRepository;
    @Autowired
    private CidadeRepository cidadeRepository;
    @Autowired
    private DepartamentoRepository departamentoRepository;

    public CidadeModel findById(Integer id) {
        return cidadeRepository.findByIdOrThr(id);
    }

    public PageOutput<CidadeModel> findAll(Pageable pageable) {
        var models = cidadeRepository.findAll(pageable);
        return new PageOutput<>(models);
    }

    public PageOutput<CidadeModel> findByNomeContaining(String nome, Pageable pageable) {
        var models = cidadeRepository.findByNomeContaining(nome, pageable);
        return new PageOutput<>(models);
    }

    public PageOutput<CidadeModel> findByDepartamentoId(Integer id, Pageable pageable) {
        var models = cidadeRepository.findByDepartamentoId(id, pageable);
        return new PageOutput<>(models);
    }

    public CidadeModel save(CidadeCreateDTO dto) {
        var departamento = departamentoRepository.findByIdOrThr(dto.idDepartamento());
        CidadeModel model = new CidadeModel(dto, departamento);
        cidadeRepository.save(model);
        return model;
    }

    public CidadeModel update(CidadeUpdateDTO dto, Integer id) {
        CidadeModel model = this.findById(id);
        model.updateValues(dto);
        cidadeRepository.save(model);
        return model;
    }

    public void delete(Integer id) {
        var model = findById(id);
        var pageable = PageRequest.of(0, 1);
        var lugares = lugarRepository.findByCidadeId(id, pageable);
        if (!lugares.isEmpty())
            throw new RestRuntimeException(HttpStatus.CONFLICT, "A cidade possui lugares registrados");
        cidadeRepository.delete(model);
    }

}