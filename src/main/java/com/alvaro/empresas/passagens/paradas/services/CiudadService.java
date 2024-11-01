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
        return models.map(CiudadDTO::new);
    }

    public Page<CiudadDTO> findByNombreContaining(String nombre, Pageable pageable) {
        Page<CiudadModel> models = ciudadRepository.findByNombreContaining(nombre, pageable);
        return models.map(CiudadDTO::new);
    }

    public CiudadModel findById(Integer id) {
        return ciudadRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, CiudadModel.class.getName()));
    }

    public CiudadDTO getOne(Integer id) {
        var model = findById(id);
        return new CiudadDTO(model);
    }

    public CiudadDTO save(CiudadDTO dto) {
        var departamento = departamentoService.findById(dto.idDepartamento());
        CiudadModel model = new CiudadModel(dto, departamento);
        ciudadRepository.save(model);
        return new CiudadDTO(model);
    }

    public CiudadDTO update(CiudadDtoUpdate dto, Integer id) {
        CiudadModel model = this.findById(id);
        model.updateValues(dto);
        ciudadRepository.save(model);
        return new CiudadDTO(model);
    }

    public void delete(CiudadModel model) {
        ciudadRepository.delete(model);
    }

}
