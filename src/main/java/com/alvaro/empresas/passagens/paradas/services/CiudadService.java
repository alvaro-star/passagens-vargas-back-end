package com.alvaro.empresas.passagens.paradas.services;

import com.alvaro.empresas.passagens.paradas.dtos.CiudadDTO;
import com.alvaro.empresas.passagens.paradas.dtos.CiudadDtoUpdate;
import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import com.alvaro.empresas.passagens.paradas.repositories.CiudadRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CiudadService {
    @Autowired
    private CiudadRepository ciudadRepository;
    @Autowired
    private DepartamentoService departamentoService;

    public Page<CiudadDTO> findAll(Pageable pageable) {
        Page<CiudadModel> models = ciudadRepository.findAll(pageable);
        return models.map(model -> new CiudadDTO(model, model.getDepartamento().getId()));
    }

    public CiudadModel findById(Integer id) {
        return ciudadRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, CiudadModel.class.getName()));
    }

    public CiudadDTO getOne(Integer id) {
        var model = ciudadRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, CiudadModel.class.getName()));
        return new CiudadDTO(model, model.getDepartamento().getId());
    }

    public CiudadDTO save(CiudadDTO dto) {
        var departamento = departamentoService.findById(dto.idDepartamento());
        CiudadModel model = new CiudadModel(dto);
        model.setDepartamento(departamento);

        var saved = ciudadRepository.save(model);
        return new CiudadDTO(saved, departamento.getId());
    }

    public CiudadDTO update(CiudadDtoUpdate dto, Integer id) {
        CiudadModel model = this.findById(id);
        model.updateValues(dto);
        var updated = ciudadRepository.save(model);
        return new CiudadDTO(updated, updated.getDepartamento().getId());
    }

    public void delete(CiudadModel model) {
        ciudadRepository.delete(model);
    }

}
