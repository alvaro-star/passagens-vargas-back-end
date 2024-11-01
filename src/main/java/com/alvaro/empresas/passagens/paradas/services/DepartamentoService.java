package com.alvaro.empresas.passagens.paradas.services;

import com.alvaro.empresas.passagens.paradas.dtos.DepartamentoDTO;
import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import com.alvaro.empresas.passagens.paradas.repositories.DepartamentoRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DepartamentoService {
    @Autowired
    private DepartamentoRepository departamentoRepository;


    public DepartamentoModel findById(Integer id) {
        Optional<DepartamentoModel> model = departamentoRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, DepartamentoModel.class.getName()));
    }

    public Page<DepartamentoDTO> findAll(Pageable pageable) {
        Page<DepartamentoModel> models = departamentoRepository.findAll(pageable);
        return models.map(DepartamentoDTO::new);
    }

    public DepartamentoModel save(DepartamentoDTO dto) {
        var model = new DepartamentoModel(dto);
        return departamentoRepository.save(model);
    }

    public DepartamentoModel update(DepartamentoDTO dto, Integer id) {
        DepartamentoModel model = this.findById(id);
        model.updateValues(dto);
        return departamentoRepository.save(model);
    }

    public void eliminar(DepartamentoModel model) {
        departamentoRepository.delete(model);
    }
}
