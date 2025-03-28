package com.alvaro.empresas.passagens.paradas.services;

import java.util.Optional;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.EntityNotFoundException;
import com.alvaro.empresas.passagens.paradas.dtos.DepartamentoOutputDTO;
import com.alvaro.empresas.passagens.paradas.repositories.CidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.paradas.dtos.DepartamentoInputDTO;
import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import com.alvaro.empresas.passagens.paradas.repositories.DepartamentoRepository;

@Service
public class DepartamentoService {
    @Autowired
    private DepartamentoRepository departamentoRepository;
    @Autowired
    private CidadeRepository cidadeRepository;

    public DepartamentoModel findById(Integer id) {
        return departamentoRepository.findByIdOrThr(id);
    }

    public Page<DepartamentoOutputDTO> findAll(Pageable pageable) {
        return departamentoRepository.findAll(pageable).map(DepartamentoOutputDTO::new);
    }

    public DepartamentoModel save(DepartamentoInputDTO dto) {
        var model = new DepartamentoModel(dto);
        return departamentoRepository.save(model);
    }

    public void update(DepartamentoInputDTO dto, Integer id) {
        DepartamentoModel model = this.findById(id);
        model.updateValues(dto);
        departamentoRepository.save(model);
    }

    public void delete(Integer id) {
        DepartamentoModel model = findById(id);

        Pageable pageable = PageRequest.of(0, 1);
        var response = cidadeRepository.findByDepartamentoId(id, pageable);
        if (response.getContent().isEmpty())
            throw new RestRuntimeException(HttpStatus.CONFLICT, "O departamento possui cidades registradas");

        departamentoRepository.delete(model);
    }
}
