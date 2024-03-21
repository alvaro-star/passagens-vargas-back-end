package com.alvaro.empresas.passagens.autobuses.services;

import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PosicionIndisponibleDTO;
import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.models.PosicionIndisponibleModel;
import com.alvaro.empresas.passagens.autobuses.repositories.PisoRepository;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
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
    private PisoRepository pisoRepository;
    @Autowired
    private PosicionIndisponibleService posicionService;

    public PisoModel findById(Long id) {
        Optional<PisoModel> model = pisoRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, PisoModel.class.getName()));
    }

    public PisoDTOResponse getOne(Long id) {
        var model = this.findById(id);
        List<Integer> posicionesBloqueadas = new ArrayList<>();

        for (PosicionIndisponibleModel posicionIndisponibleModel : model.getPosicionesIndisponibles()) {
            posicionesBloqueadas.add(posicionIndisponibleModel.getNumero());
        }

        long idAutobus = model.getAutobus().getId();

        return new PisoDTOResponse(model, idAutobus, posicionesBloqueadas);
    }

    public Page<PisoDTOResponse> findAll(Pageable pageable) {
        Page<PisoModel> pisos = pisoRepository.findAll(pageable);
        return pisos.map(piso -> {
            List<Integer> posicionesIndisponibles = new ArrayList<>();
            for (PosicionIndisponibleModel posicionesIndisponible : piso.getPosicionesIndisponibles()) {
                posicionesIndisponibles.add(posicionesIndisponible.getNumero());
            }
            return new PisoDTOResponse(piso, piso.getAutobus().getId(), posicionesIndisponibles);
        });
    }

    @Transactional
    public PisoDTOResponse salvar(PisoDTO dto, AutobusModel autobusModel, Integer nPiso, Integer nPrimeraSilla) {

        int produto = dto.getNLinhas() * dto.getNColunas();
        for (Integer posicion : dto.getPosicoesIndisponiveis()) {
            if (posicion > produto) {
                throw new ValidationException("Las posiciones indisponibles son invalidas");
            }
        }

        var pisoModel = new PisoModel(dto, nPiso, nPrimeraSilla);
        pisoModel.setAutobus(autobusModel);
        var saved = pisoRepository.save(pisoModel);

        List<Integer> bloqueadoDTOS = posicionService.saveAll(dto.getPosicoesIndisponiveis(), saved);
        return new PisoDTOResponse(saved, autobusModel.getId(), bloqueadoDTOS);
    }

    @Transactional
    public PisoDTOResponse update(PisoDTOUpdate dto, Long id) {
        var model = this.findById(id);

        if (!model.getAutobus().getTrayectos().isEmpty()) {
            return null;
        }

        int produto = dto.getNLinhas() * dto.getNColunas();
        for (Integer posicion : dto.getPosicoesIndisponiveis()) {
            if (posicion > produto) {
                return null;
            }
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

        posicionService.deleteAll(model.getPosicionesIndisponibles());
        model.setPosicionesIndisponibles(new ArrayList<PosicionIndisponibleModel>());

        var modelUpdate = pisoRepository.save(model);
        List<Integer> bloqueadoDTOS = posicionService.saveAll(dto.getPosicoesIndisponiveis(), modelUpdate);
        return new PisoDTOResponse(modelUpdate, modelUpdate.getAutobus().getId(), bloqueadoDTOS);
    }
}
