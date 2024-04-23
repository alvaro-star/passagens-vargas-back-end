package com.alvaro.empresas.passagens.autobuses.services;

import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.repositories.PisoRepository;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PisoService {
    @Autowired
    private PisoRepository pisoRepository;

    public PisoModel findById(Integer id) {
        Optional<PisoModel> model = pisoRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, PisoModel.class.getName()));
    }

    public PisoDTOResponse getOne(Integer id) {
        var model = this.findById(id);
        Integer idAutobus = model.getAutobus().getId();
        return new PisoDTOResponse(model, idAutobus);
    }

    public Page<PisoDTOResponse> findAll(Pageable pageable) {
        Page<PisoModel> pisos = pisoRepository.findAll(pageable);
        return pisos.map(piso -> new PisoDTOResponse(piso, piso.getAutobus().getId()));
    }

    @Transactional
    public PisoDTOResponse salvar(PisoDTO dto, AutobusModel autobusModel, Integer nPiso, Integer nPrimeraSilla) {
        int nSillas = dto.getNLinhas() * dto.getNColunas();
        for (Integer posicion : dto.getPosicoesIndisponiveis()) {
            if (posicion > nSillas)
                throw new ValidationException("Las posiciones indisponibles son invalidas");
        }

        var pisoModel = new PisoModel(dto, nPiso, nPrimeraSilla);
        pisoModel.setAutobus(autobusModel);
        var saved = pisoRepository.save(pisoModel);

        return new PisoDTOResponse(saved, autobusModel.getId());
    }

    @Transactional
    public PisoDTOResponse update(PisoDTOUpdate dto, Integer id) {
        var model = this.findById(id);

        if (!model.getAutobus().getViajes().isEmpty())
            return null;

        int produto = dto.getNLinhas() * dto.getNColunas();
        for (Integer posicion : dto.getPosicoesIndisponiveis()) {
            if (posicion > produto)
                return null;
        }

        model.updateValues(dto);

        if (model.getAutobus().getPisos().size() == 2) {
            int indiceSegundoPiso = (model.getAutobus().getPisos().get(0).getNPiso() == 2) ? 0 : 1;
            if (model.getNPiso() == 1) {
                var segundoPisoModel = model.getAutobus().getPisos().get(indiceSegundoPiso);
                segundoPisoModel.setPrimeraSilla(model.getNSillas() + 1);
                pisoRepository.save(segundoPisoModel);
            }
        }


        var modelUpdate = pisoRepository.save(model);
        return new PisoDTOResponse(modelUpdate, modelUpdate.getAutobus().getId());
    }
}
