package com.alvaro.empresas.passagens.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alvaro.empresas.passagens.configuracoes.validations.services.ValidEnabledEntities;
import com.alvaro.empresas.passagens.dtos.precos.PrecoResponseDTO;
import com.alvaro.empresas.passagens.dtos.precos.PrecoUpdateDTO;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.repositories.PrecoRepository;

@Service
public class PrecoService {
    @Autowired
    private PrecoRepository precoRepository;
    @Autowired
    private UserLoguedComponent userLogued;

    public List<PrecoResponseDTO> saveAll(List<PrecoModel> newModels, ViagemModel viagem) {
        for (PrecoModel newModel : newModels) {
            newModel.setViagem(viagem);
            newModel.setEmpresa(viagem.getEmpresa());
        }
        precoRepository.saveAll(newModels);
        List<PrecoResponseDTO> salvos = new ArrayList<>();
        newModels.forEach(model -> salvos.add(new PrecoResponseDTO(model)));
        return salvos;
    }

    public PrecoResponseDTO findById(UUID id) {
        var model = precoRepository.findByIdOrThr(id);
        return new PrecoResponseDTO(model);
    }

    public PrecoResponseDTO update(UUID id, PrecoUpdateDTO dto) {
        var model = precoRepository.findByIdOrThr(id);
        userLogued.validIfIsMyEmpresa(model.getEmpresaId());
        ValidEnabledEntities.validEmpresa(model.getEmpresa());

        model.updateValues(dto);
        precoRepository.save(model);
        return new PrecoResponseDTO(model);
    }
}
