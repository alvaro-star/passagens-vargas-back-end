package com.alvaro.empresas.passagens.paradas.services;

import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.models.TrayectoModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOUpdate;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.services.TrayectoService;
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
    private TrayectoService trayectoService;
    @Autowired
    private LugarService lugarService;

    public ParadaModel findById(Integer id) {
        var model = paradaRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, ParadaModel.class.getName()));
    }

    public ParadaDTO getOne(Integer id) {
        var model = this.findById(id);
        int idLugar = model.getLugar().getId();
        UUID idTrayecto = model.getTrayecto().getCodigo();
        return new ParadaDTO(model, idLugar, idTrayecto);
    }

    public Page<ParadaDTO> getAll(Pageable pageable) {
        Page<ParadaModel> models = paradaRepository.findAll(pageable);
        return models.map(model -> {
            int idLugar = model.getLugar().getId();
            UUID idTrayecto = model.getTrayecto().getCodigo();
            return new ParadaDTO(model, idLugar, idTrayecto);
        });
    }

    public ParadaDTO save(ParadaDTO dtoSended) {
        LugarModel lugar = lugarService.findById(dtoSended.idLugar());
        TrayectoModel trayecto = trayectoService.findById(dtoSended.idTrayecto());

        for (ParadaModel parada : trayecto.getParadas()) {
            if (parada.getDataHora().isEqual(dtoSended.dataHora())) {
                throw new ValidationException(new FieldMessage("dataHora", "Ya hay una parada registrada en esta fecha"));
            }
        }

        var model = new ParadaModel(dtoSended);
        model.setLugar(lugar);
        model.setTrayecto(trayecto);

        var modelSave = paradaRepository.save(model);
        return new ParadaDTO(modelSave, lugar.getId(), trayecto.getCodigo());
    }

    public ParadaDTO update(ParadaDTOUpdate dtoSended, Integer id) {
        var model = this.findById(id);
        model.updateValues(dtoSended);

        for (ParadaModel parada : model.getTrayecto().getParadas()) {
            if (parada.getDataHora().isEqual(dtoSended.dataHora())) {
                throw new ValidationException(new FieldMessage("dataHora", "Ya hay una parada registrada en esta fecha"));
            }
        }

        if (dtoSended.idLugar() != null) {
            LugarModel lugar = lugarService.findById(dtoSended.idLugar());
            model.setLugar(lugar);
        }


        var modelUpdated = paradaRepository.save(model);
        UUID idTrayecto = modelUpdated.getTrayecto().getCodigo();
        return new ParadaDTO(modelUpdated, modelUpdated.getLugar().getId(), idTrayecto);
    }

    public void delete(ParadaModel model) {
        paradaRepository.delete(model);
    }
}
