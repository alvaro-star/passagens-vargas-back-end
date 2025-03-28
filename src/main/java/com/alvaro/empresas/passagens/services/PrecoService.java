package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.dtos.precos.PrecoDTOResponseViagem;
import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.onibus.models.PisoModel;
import com.alvaro.empresas.passagens.dtos.precos.PrecoDTO;
import com.alvaro.empresas.passagens.dtos.precos.PrecoDTOUpdate;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.repositories.PassagemRepository;
import com.alvaro.empresas.passagens.repositories.PrecoRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PrecoService {
    @Autowired
    private PrecoRepository precoRepository;
    @Autowired
    private PassagemRepository passagemRepository;

    public PrecoModel findById(UUID id) {
        var model = precoRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, PrecoModel.class.getName()));
    }

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

    public PrecoDTO getOne(UUID id) {
        var model = findById(id);
        return new PrecoDTO(model);
    }

    public PrecoDTOResponseViagem vender(UUID id) {
        var model = findById(id);
        List<PisoModel> pisos = model.getViagem().getOnibus().getPisos();
        var pisoElegido = new PisoModel();

        for (PisoModel piso : pisos)
            if (piso.getNPiso().equals(model.getNPiso())) pisoElegido = piso;

        PisoDTOResponse pisoDto = new PisoDTOResponse(pisoElegido);
        List<Integer> ocupados = passagemRepository.getPassagensVendidasENaoReembolsadas(model.getId());
        return new PrecoDTOResponseViagem(model, pisoDto, ocupados);
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
