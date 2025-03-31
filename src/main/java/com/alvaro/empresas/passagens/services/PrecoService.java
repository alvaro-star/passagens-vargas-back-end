package com.alvaro.empresas.passagens.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alvaro.empresas.passagens.dtos.precos.PrecoDTO;
import com.alvaro.empresas.passagens.dtos.precos.PrecoDTOResponseViagem;
import com.alvaro.empresas.passagens.dtos.precos.PrecoDTOUpdate;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoResponseDTO;
import com.alvaro.empresas.passagens.onibus.models.PisoModel;
import com.alvaro.empresas.passagens.repositories.PassagemRepository;
import com.alvaro.empresas.passagens.repositories.PrecoRepository;

@Service
public class PrecoService {
    @Autowired
    private PrecoRepository precoRepository;
    @Autowired
    private PassagemRepository passagemRepository;


    public List<PrecoDTO> saveAll(List<PrecoModel> newModels, ViagemModel viagem) {
        for (PrecoModel newModel : newModels) {
            newModel.setViagem(viagem);
            newModel.setEmpresa(viagem.getEmpresa());
        }
        precoRepository.saveAll(newModels);
        List<PrecoDTO> salvos = new ArrayList<>();
        newModels.forEach(model -> salvos.add(new PrecoDTO(model)));
        return salvos;
    }

    public PrecoDTO findById(UUID id) {
        var model = precoRepository.findByIdOrThr(id);
        return new PrecoDTO(model);
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

    public PrecoDTO update(PrecoDTOUpdate dto, PrecoModel model) {
        model.updateValues(dto);
        precoRepository.save(model);
        return new PrecoDTO(model);
    }

    public void updateFromService(PrecoModel precoModel) {
        precoRepository.save(precoModel);
    }
}
