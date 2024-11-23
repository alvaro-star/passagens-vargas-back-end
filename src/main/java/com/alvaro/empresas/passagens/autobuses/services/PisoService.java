package com.alvaro.empresas.passagens.autobuses.services;

import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOCreate;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.repositories.PisoRepository;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.helpers.validators.AutobusEnabled;
import com.alvaro.empresas.passagens.helpers.validators.EmpresaEnabled;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PisoService {
    @Autowired
    private EmpresaEnabled empresaEnabled;
    @Autowired
    private AutobusEnabled autobusService;
    @Autowired
    private PisoRepository pisoRepository;
    @Autowired
    private ViajeRepository viajeRepository;

    public PisoModel findById(Integer id) {
        Optional<PisoModel> model = pisoRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, PisoModel.class.getName()));
    }

    public PisoDTOResponse getOne(Integer id) {
        var model = this.findById(id);
        return new PisoDTOResponse(model);
    }

    public Page<PisoDTOResponse> findAll(Pageable pageable) {
        Page<PisoModel> pisos = pisoRepository.findAll(pageable);
        return pisos.map(PisoDTOResponse::new);
    }

    @Transactional
    public PisoDTOResponse salvar(PisoDTOCreate dto, AutobusModel autobusModel, Integer nPiso, Integer nPrimeraSilla) {
        int nSillas = dto.getNSillas();
        for (Integer posicion : dto.getPosicionesBloqueadas()) {
            if (posicion > nSillas)
                throw new ValidationException("Las posiciones indisponibles son invalidas");
        }

        var pisoModel = new PisoModel(dto, nPiso, nPrimeraSilla);
        pisoModel.setAutobus(autobusModel);
        var saved = pisoRepository.save(pisoModel);
        return new PisoDTOResponse(saved);
    }

    @Transactional
    public PisoDTOResponse update(PisoDTOUpdate dto, PisoModel model) {
        autobusService.validAutobusEnabled(model.getId());
        empresaEnabled.validEmpresaEnabled(model.getAutobus().getEmpresaId());
        var viaje = viajeRepository.findFirst1ByAutobusId(model.getAutobus().getId());
        int nSillas = dto.getNSillas();
        if (viaje.isPresent())
            throw new ValidationException("El autobus ya tiene un viaje registrado");
        for (Integer posicion : dto.getPosicoesIndisponiveis())
            if (posicion > nSillas)
                throw new ValidationException("Una posicion informada es invalida");
        List<PisoModel> pisos = new ArrayList<>();
        model.updateValues(dto);
        pisos.add(model);
        if (model.getAutobus().getPisos().size() == 2 && model.getNPiso() == 1) {
            var segundoPisoModel = model.getAutobus().getPisoByNumero(2);
            segundoPisoModel.setPrimeraSilla(model.getNSillas() + 1);
            pisos.add(segundoPisoModel);
        }

        pisoRepository.saveAll(pisos);
        return new PisoDTOResponse(model);
    }
}
