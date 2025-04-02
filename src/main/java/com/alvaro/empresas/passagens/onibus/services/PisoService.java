package com.alvaro.empresas.passagens.onibus.services;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.helpers.validators.ValidEnabledEntities;
import com.alvaro.empresas.passagens.onibus.dtos.PisoResponseDTO;
import com.alvaro.empresas.passagens.onibus.dtos.PisoUpdateDTO;
import com.alvaro.empresas.passagens.onibus.models.PisoModel;
import com.alvaro.empresas.passagens.onibus.repositories.PisoRepository;

import com.alvaro.empresas.passagens.repositories.ViagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PisoService {
    @Autowired
    private PisoRepository pisoRepository;
    @Autowired
    private ViagemRepository viagemRepository;
    @Autowired
    private UserLoguedComponent userLogued;

    public PisoResponseDTO findById(UUID id) {
        var model = pisoRepository.findByIdOrThr(id);
        return new PisoResponseDTO(model);
    }

    @Transactional
    public PisoResponseDTO update(UUID id, PisoUpdateDTO dto) {
        var model = pisoRepository.findByIdOrThr(id);
        var onibus = model.getOnibus();
        var empresa = onibus.getEmpresa();

        userLogued.validIfIsMyEmpresa(model.getOnibus().getEmpresaId());
        ValidEnabledEntities.validOnibus(onibus);
        ValidEnabledEntities.validEmpresa(empresa);

        var viagem = viagemRepository.findFirst1ByOnibusId(model.getOnibus().getId());
        int nAssentos = dto.getNAssentos();
        if (viagem.isPresent())
            throw new RestRuntimeException(HttpStatus.CONFLICT, "O ônibus já tem uma viagem registrada");
        for (Integer posicao : dto.getPosicoesIndisponiveis())
            if (posicao > nAssentos)
                throw new RestRuntimeException(HttpStatus.CONFLICT, "Uma posição informada é inválida");
        List<PisoModel> pisos = new ArrayList<>();
        model.updateValues(dto);
        pisos.add(model);
        if (model.getOnibus().getPisos().size() == 2 && model.getNPiso() == 1) {
            var segundoPisoModel = model.getOnibus().getPisoByNumero(2);
            segundoPisoModel.setPrimeiroAssento(model.getNAssentos() + 1);
            pisos.add(segundoPisoModel);
        }

        pisoRepository.saveAll(pisos);
        return new PisoResponseDTO(model);
    }
}