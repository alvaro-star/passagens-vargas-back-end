package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.dtos.EmpresaDto;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.repositories.EmpresaRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {
    @Autowired
    private EmpresaRepository empresaRepository;

    public EmpresaModel findById(Integer id) {
        Optional<EmpresaModel> model = empresaRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, EmpresaModel.class.getName()));
    }

    public List<EmpresaModel> findAll() {
        return empresaRepository.findAll();
    }

    public List<EmpresaDto> modelsDtoToListDtos() {
        List<EmpresaDto> dtos = new ArrayList<EmpresaDto>();
        List<EmpresaModel> models = findAll();
        if (!models.isEmpty()) {
            for (EmpresaModel model : models) {
                dtos.add(new EmpresaDto(model));
            }
        }
        return dtos;
    }

    public EmpresaModel save(EmpresaDto dto) {
        var model = new EmpresaModel();
        BeanUtils.copyProperties(model, dto, "id");
        return empresaRepository.save(model);
    }

    public EmpresaModel update(EmpresaDto dto, Integer id) {
        var model = this.findById(id);
        BeanUtils.copyProperties(model, dto, "id");
        return empresaRepository.save(model);
    }

    public void delete(Integer id) {
        var model = this.findById(id);
        empresaRepository.delete(model);
    }

}
