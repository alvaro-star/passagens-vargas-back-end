package com.alvaro.empresas.passagens.onibus.services;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.helpers.validators.ValidEnabledEntities;
import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoDTOCreate;
import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoDTOUpdate;
import com.alvaro.empresas.passagens.onibus.models.OnibusModel;
import com.alvaro.empresas.passagens.onibus.models.PisoModel;
import com.alvaro.empresas.passagens.onibus.repositories.PisoRepository;

import com.alvaro.empresas.passagens.repositories.ViagemRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PisoService {
    @Autowired
    private OnibusService onibusService;
    @Autowired
    private PisoRepository pisoRepository;
    @Autowired
    private ViagemRepository viagemRepository;

    public PisoModel findById(UUID id) {
        Optional<PisoModel> model = pisoRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, PisoModel.class.getName()));
    }

    public PisoDTOResponse getOne(UUID id) {
        var model = this.findById(id);
        return new PisoDTOResponse(model);
    }

    public Page<PisoDTOResponse> findAll(Pageable pageable) {
        Page<PisoModel> pisos = pisoRepository.findAll(pageable);
        return pisos.map(PisoDTOResponse::new);
    }

    @Transactional
    public PisoDTOResponse salvar(PisoDTOCreate dto, OnibusModel onibusModel, Integer nPiso, Integer nPrimeiroAssento) {
        int nAssentos = dto.getNAssentos();
        for (Integer posicao : dto.getPosicoesBloquedas()) {
            if (posicao > nAssentos)
                throw new ValidationException("As posições indisponíveis são inválidas");
        }

        var pisoModel = new PisoModel(dto, nPiso, nPrimeiroAssento);
        pisoModel.setOnibus(onibusModel);
        var saved = pisoRepository.save(pisoModel);
        return new PisoDTOResponse(saved);
    }

    @Transactional
    public PisoDTOResponse update(PisoDTOUpdate dto, PisoModel model) {
        var onibus = onibusService.findById(model.getOnibusId());
        var empresa = onibus.getEmpresa();
        ValidEnabledEntities.validOnibus(onibus);
        ValidEnabledEntities.validEmpresa(empresa);

        var viagem = viagemRepository.findFirst1ByOnibusId(model.getOnibus().getId());
        int nAssentos = dto.getNAssentos();
        if (viagem.isPresent())
            throw new ValidationException("O ônibus já tem uma viagem registrada");
        for (Integer posicao : dto.getPosicoesIndisponiveis())
            if (posicao > nAssentos)
                throw new ValidationException("Uma posição informada é inválida");
        List<PisoModel> pisos = new ArrayList<>();
        model.updateValues(dto);
        pisos.add(model);
        if (model.getOnibus().getPisos().size() == 2 && model.getNPiso() == 1) {
            var segundoPisoModel = model.getOnibus().getPisoByNumero(2);
            segundoPisoModel.setPrimeiroAssento(model.getNAssentos() + 1);
            pisos.add(segundoPisoModel);
        }

        pisoRepository.saveAll(pisos);
        return new PisoDTOResponse(model);
    }
}