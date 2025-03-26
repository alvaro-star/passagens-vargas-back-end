package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.dtos.precos.PrecioDTOResponseViaje;
import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.onibus.models.PisoModel;
import com.alvaro.empresas.passagens.dtos.precos.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.precos.PrecioDTOUpdate;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import com.alvaro.empresas.passagens.repositories.PrecioRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PrecioService {
    @Autowired
    private PrecioRepository precioRepository;
    @Autowired
    private PasajeRepository pasajeRepository;

    public PrecoModel findById(UUID id) {
        var model = precioRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, PrecoModel.class.getName()));
    }

    public List<PrecioDTO> saveAll(List<PrecoModel> newModels, ViagemModel viagem) {
        for (PrecoModel newModel : newModels) {
            newModel.setViagem(viagem);
            newModel.setEmpresa(viagem.getEmpresa());
        }
        precioRepository.saveAll(newModels);
        List<PrecioDTO> salvos = new ArrayList<>();
        newModels.forEach(model -> salvos.add(new PrecioDTO(model)));
        return salvos;
    }

    public PrecioDTO getOne(UUID id) {
        var model = findById(id);
        return new PrecioDTO(model);
    }

    public PrecioDTOResponseViaje vender(UUID id) {
        var model = findById(id);
        List<PisoModel> pisos = model.getViagem().getAutobus().getPisos();
        var pisoElegido = new PisoModel();

        for (PisoModel piso : pisos)
            if (piso.getNPiso().equals(model.getNPiso())) pisoElegido = piso;

        PisoDTOResponse pisoDto = new PisoDTOResponse(pisoElegido);
        List<Integer> ocupados = pasajeRepository.getPasajesVendidosAndNoRembolso(model.getId());
        return new PrecioDTOResponseViaje(model, pisoDto, ocupados);
    }

    public PrecioDTO update(PrecioDTOUpdate dto, PrecoModel model) {
        model.updateValues(dto);
        precioRepository.save(model);
        return new PrecioDTO(model);
    }

    public void updateFromService(PrecoModel precoModel) {
        precioRepository.save(precoModel);
    }
}
