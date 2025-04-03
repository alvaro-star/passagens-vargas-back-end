package com.alvaro.empresas.passagens.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alvaro.empresas.passagens.dtos.precos.PrecoResponseDTO;
import com.alvaro.empresas.passagens.dtos.precos.PrecoDTOResponseViagem;
import com.alvaro.empresas.passagens.dtos.precos.PrecoUpdateDTO;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.helpers.validations.ValidEnabledEntities;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.onibus.dtos.PisoResponseDTO;
import com.alvaro.empresas.passagens.onibus.models.PisoModel;
import com.alvaro.empresas.passagens.repositories.PassagemRepository;
import com.alvaro.empresas.passagens.repositories.PrecoRepository;

@Service
public class PrecoService {
    @Autowired
    private PrecoRepository precoRepository;
    @Autowired
    private PassagemRepository passagemRepository;
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

    public PrecoDTOResponseViagem vender(UUID id) {
        var model = precoRepository.findByIdOrThr(id);
        List<PisoModel> pisos = model.getViagem().getOnibus().getPisos();
        var pisoElegido = new PisoModel();

        for (PisoModel piso : pisos)
            if (piso.getNPiso().equals(model.getNPiso()))
                pisoElegido = piso;

        PisoResponseDTO pisoDTO = new PisoResponseDTO(pisoElegido);
        List<Integer> ocupados = passagemRepository.getPassagensVendidasENaoReembolsadas(model.getId());
        return new PrecoDTOResponseViagem(model, pisoDTO, ocupados);
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
