package com.alvaro.empresas.passagens.autobuses.services;

import com.alvaro.empresas.passagens.autobuses.dtos.PisoDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.PisoDTOResponse;
import com.alvaro.empresas.passagens.autobuses.dtos.PisoDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.dtos.PosicionIndisponibleDTO;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.models.PosicionIndisponibleModel;
import com.alvaro.empresas.passagens.autobuses.repositories.PisoRepository;
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
    @Autowired
    private AutobusService autobusService;

    public PisoModel findById(Integer id) {
        Optional<PisoModel> model = pisoRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, PisoModel.class.getName()));
    }

    public PisoDTOResponse getOne(Integer id) {
        var model = this.findById(id);
        List<PosicionIndisponibleDTO> posicionesBloqueadas = new ArrayList<>();

        for (PosicionIndisponibleModel posicionIndisponibleModel : model.getPosicionesIndisponibles()) {
            posicionesBloqueadas.add(new PosicionIndisponibleDTO(posicionIndisponibleModel));
        }

        int idAutobus = model.getAutobus().getId();

        return new PisoDTOResponse(model, idAutobus, posicionesBloqueadas);
    }

    public Page<PisoDTOResponse> findAll(Pageable pageable) {
        Page<PisoModel> pisos = pisoRepository.findAll(pageable);
        return pisos.map(piso -> {
            List<PosicionIndisponibleDTO> posicionesIndisponibles = new ArrayList<>();
            for (PosicionIndisponibleModel posicionesIndisponible : piso.getPosicionesIndisponibles()) {
                posicionesIndisponibles.add(new PosicionIndisponibleDTO(posicionesIndisponible));
            }
            return new PisoDTOResponse(piso, piso.getAutobus().getId(), posicionesIndisponibles);
        });
    }

    @Transactional
    public PisoDTOResponse save(PisoDTO dto) {
        var autobus = autobusService.findById(dto.getIdAutobus());
        //Poco eficiente, pueden traer muchos!!!! trayectos
        if (!autobus.getTrayectos().isEmpty()) {
            return null;
        }

        int produto = dto.getNLinhas() * dto.getNColunas();
        for (PosicionIndisponibleDTO posicionDTO : dto.getPosicoesIndisponiveis()) {
            if (posicionDTO.numero() > produto) {
                return null;
            }
        }
        //En caso de que el autobus ya haya hecho algun trayecto, no se podran aumentar pisos
        int novoNumeroPiso;
        int nPrimeraSilla;
        switch (autobus.getPisos().size()) {
            case 0:
                novoNumeroPiso = 1;
                nPrimeraSilla = 1;
                break;
            case 1:
                novoNumeroPiso = 2;
                nPrimeraSilla = autobus.getPisos().get(0).getNSillas();
                break;
            default:
                return null;
        }

        var pisoModel = new PisoModel(dto, novoNumeroPiso, nPrimeraSilla);
        pisoModel.setAutobus(autobus);
        var saved = pisoRepository.save(pisoModel);

        List<PosicionIndisponibleDTO> bloqueadoDTOS = posicionService.saveAll(dto.getPosicoesIndisponiveis(), saved);

        //List<AsientoBloqueadoDTO> bloqueadoDTOS = asientoBloqueadosService.convertModelsToDtos(saved.getPosicionesIndisponibles());
        return new PisoDTOResponse(saved, autobus.getId(), bloqueadoDTOS);
    }

    @Transactional
    public PisoDTOResponse update(PisoDTOUpdate dto, Integer id) {
        var model = this.findById(id);

        if (!model.getAutobus().getTrayectos().isEmpty()) {
            return null;
        }

        int produto = dto.getNLinhas() * dto.getNColunas();
        for (PosicionIndisponibleDTO posicionDTO : dto.getPosicoesIndisponiveis()) {
            if (posicionDTO.numero() > produto) {
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
        List<PosicionIndisponibleDTO> bloqueadoDTOS = posicionService.saveAll(dto.getPosicoesIndisponiveis(), modelUpdate);

        return new PisoDTOResponse(modelUpdate, modelUpdate.getAutobus().getId(), bloqueadoDTOS);
    }

    @Transactional
    public void delete(PisoModel model) {
        //Este Metodo devido ao efeito cascada elimina os assientos bloqueados
        pisoRepository.delete(model);
    }
}
