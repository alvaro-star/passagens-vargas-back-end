package com.alvaro.empresas.passagens.lugares.services;

import com.alvaro.empresas.passagens.lugares.dtos.DepartamentoDTO;
import com.alvaro.empresas.passagens.lugares.models.DepartamentoModel;
import com.alvaro.empresas.passagens.lugares.repositories.DepartamentoRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class DepartamentoService {
    @Autowired
    private DepartamentoRepository departamentoRepository;

    public List<DepartamentoModel> findAll() {
        return departamentoRepository.findAll();
    }

    public DepartamentoModel findById(Integer id) {
        Optional<DepartamentoModel> model = departamentoRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, DepartamentoModel.class.getName()));
    }

    public DepartamentoModel save(DepartamentoDTO dto) {
        var model = new DepartamentoModel(dto);
        return departamentoRepository.save(model);
    }

    public DepartamentoModel update(DepartamentoDTO dto, Integer id) {
        DepartamentoModel model = this.findById(id);
        model.setNombre(dto.getNombre());
        return departamentoRepository.save(model);
    }

    public void eliminar(DepartamentoModel model) {
        departamentoRepository.delete(model);
    }
}
