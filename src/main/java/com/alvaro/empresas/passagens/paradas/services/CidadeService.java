package com.alvaro.empresas.passagens.paradas.services;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.paradas.dtos.CidadeDTO;
import com.alvaro.empresas.passagens.paradas.dtos.CidadeDTOUpdate;
import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import com.alvaro.empresas.passagens.paradas.repositories.CidadeRepository;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CidadeService {
    @Autowired
    private CidadeRepository cidadeRepository;
    @Autowired
    private DepartamentoService departamentoService;

    public Page<CidadeDTO> findAll(Pageable pageable) {
        Page<CidadeModel> models = cidadeRepository.findAll(pageable);
        return models.map(CidadeDTO::new);
    }

    public Page<CidadeDTO> findByNomeContaining(String nome, Pageable pageable) {
        Page<CidadeModel> models = cidadeRepository.findByNomeContaining(nome, pageable);
        return models.map(CidadeDTO::new);
    }

    public Page<CidadeModel> findByDepartamentoId(Integer id, Pageable pageable) {
        return cidadeRepository.findByDepartamentoId(id, pageable);
    }

    public CidadeModel findById(Integer id) {
        return cidadeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, CidadeModel.class));
    }

    public CidadeDTO save(CidadeDTO dto) {
        var departamento = departamentoService.findById(dto.idDepartamento());
        CidadeModel model = new CidadeModel(dto, departamento);
        cidadeRepository.save(model);
        return new CidadeDTO(model);
    }

    public CidadeDTO update(CidadeDTOUpdate dto, Integer id) {
        CidadeModel model = this.findById(id);
        model.updateValues(dto);
        cidadeRepository.save(model);
        return new CidadeDTO(model);
    }

    public void delete(Integer id) {
        var model = findById(id);
        if (!model.getLugares().isEmpty())
            throw new RestRuntimeException(HttpStatus.BAD_REQUEST, "A cidade possui lugares registrados");
        cidadeRepository.delete(model);
    }
}