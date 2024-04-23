package com.alvaro.empresas.passagens.paradas.services;

import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOUpdate;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.services.ViajeService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ParadaService {
    @Autowired
    private ParadaRepository paradaRepository;
    @Autowired
    private ViajeService viajeService;
    @Autowired
    private LugarService lugarService;

    public ParadaModel findById(Integer id) {
        var model = paradaRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, ParadaModel.class.getName()));
    }

    public ParadaDTOComplete getOne(Integer id) {
        var model = this.findById(id);
        UUID idViaje = model.getViaje().getCodigo();
        return new ParadaDTOComplete(model, idViaje);
    }

    public Page<ParadaDTO> getAll(Pageable pageable) {
        Page<ParadaModel> models = paradaRepository.findAll(pageable);
        return models.map(model -> {
            int idLugar = model.getLugar().getId();
            UUID idViaje = model.getViaje().getCodigo();
            return new ParadaDTO(model, idLugar, idViaje);
        });
    }

    public ParadaDTO save(ParadaDTO dtoSended) {
        LugarModel lugar = lugarService.findById(dtoSended.idLugar());
        if (!lugar.getEnable())
            throw new ValidationException("idLugar", "El lugar no esta disponible");

        ViajeModel viaje = viajeService.findById(dtoSended.idViaje());

        for (ParadaModel parada : viaje.getParadas()) {
            if (parada.getDataHora().isEqual(dtoSended.dataHora()))
                throw new ValidationException(new FieldMessage("dataHora", "Ya hay una parada registrada en esta fecha"));
            if (parada.getLugar().getId() == dtoSended.idLugar())
                throw new ValidationException("idLugar", "Ya hay una parada registrada que passara por este lugar");
        }

        if (!viaje.dataHoraValido(dtoSended.dataHora()))
            throw new ValidationException("dataHora", "La parada no puede ser maior o menor que las dos primeras");

        var model = new ParadaModel(dtoSended);
        model.setLugar(lugar);
        model.setViaje(viaje);

        var modelSave = paradaRepository.save(model);
        return new ParadaDTO(modelSave, lugar.getId(), viaje.getCodigo());
    }

    public ParadaDTO update(ParadaDTOUpdate dtoSended, Integer id) {
        var model = this.findById(id);

        for (ParadaModel parada : model.getViaje().getParadas()) {
            if (parada.getDataHora().isEqual(dtoSended.dataHora()))
                throw new ValidationException("dataHora", "Ya hay una parada registrada en esta fecha");

            System.out.println("\n" + parada.getLugar().getId() + " - " + dtoSended.idLugar());
            if (parada.getLugar().getId() == dtoSended.idLugar())
                throw new ValidationException("idLugar", "Ya hay una parada registrada que passara por este lugar");
        }
        if (!model.getViaje().dataHoraValido(dtoSended.dataHora()))
            throw new ValidationException("dataHora", "La parada no puede ser maior o menor que las paradas extremas");

        model.updateValues(dtoSended);

        if (dtoSended.idLugar() != null) {
            LugarModel lugar = lugarService.findById(dtoSended.idLugar());
            if (!lugar.getEnable())
                throw new ValidationException("idLugar", "El lugar no esta disponible");
            model.setLugar(lugar);
        }


        var modelUpdated = paradaRepository.save(model);
        UUID idViaje = modelUpdated.getViaje().getCodigo();
        return new ParadaDTO(modelUpdated, modelUpdated.getLugar().getId(), idViaje);
    }

    public void delete(ParadaModel model) {
        paradaRepository.delete(model);
    }
}
