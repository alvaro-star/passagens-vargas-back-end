package com.alvaro.empresas.passagens.lugares.services;

import com.alvaro.empresas.passagens.lugares.dtos.CiudadDTO;
import com.alvaro.empresas.passagens.lugares.dtos.CiudadDtoUpdate;
import com.alvaro.empresas.passagens.lugares.models.CiudadModel;
import com.alvaro.empresas.passagens.lugares.models.DepartamentoModel;
import com.alvaro.empresas.passagens.lugares.repositories.CiudadRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CiudadService {
    @Autowired
    private CiudadRepository ciudadRepository;

    public List<CiudadModel> findAll() {
        return ciudadRepository.findAll();
    }

    public CiudadModel findById(Integer id) {
        var model = ciudadRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, CiudadModel.class.getName()));
    }

    public CiudadModel save(CiudadDTO dto, DepartamentoModel departamento) {
        CiudadModel model = new CiudadModel(dto);
        model.setDepartamento(departamento);
        return ciudadRepository.save(model);
    }

    public CiudadModel update(CiudadDtoUpdate dto, Integer id) {
        CiudadModel model = this.findById(id);
        model.setNombre(dto.getNombre());
        return ciudadRepository.save(model);
    }

    public void delete(CiudadModel model) {
        ciudadRepository.delete(model);
    }

}
