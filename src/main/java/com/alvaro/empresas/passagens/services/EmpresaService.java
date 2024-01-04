package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.dtos.EmpresaDto;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.repositories.EmpresaRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class EmpresaService {
    @Autowired
    private EmpresaRepository empresaRepository;

    public EmpresaModel findById(Integer id) {
        Optional<EmpresaModel> model = empresaRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, EmpresaModel.class.getName()));
    }

    public Page<EmpresaDto> findAll(Pageable pageable) {
        return empresaRepository.findAll(pageable).map(EmpresaDto::new);
    }

    @Transactional
    public EmpresaModel save(EmpresaDto dto) {
        var model = new EmpresaModel();
        BeanUtils.copyProperties(dto, model, "id", "autobuses");
        return empresaRepository.save(model);
    }

    public EmpresaModel update(EmpresaDto dto, Integer id) {
        var model = this.findById(id);
        BeanUtils.copyProperties(dto, model, "id", "autobuses");
        return empresaRepository.save(model);
    }

    @Transactional
    public void delete(Integer id) {
        var model = this.findById(id);
        empresaRepository.delete(model);
    }

}
