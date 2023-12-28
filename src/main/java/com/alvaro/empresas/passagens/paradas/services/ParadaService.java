package com.alvaro.empresas.passagens.paradas.services;

import com.alvaro.empresas.passagens.models.TrayectoModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOUpdate;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.services.TrayectoService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public List<ParadaDTO> getAll() {
        List<ParadaModel> models = paradaRepository.findAll();
        List<ParadaDTO> dtos = new ArrayList<>();
        for (ParadaModel model : models) {
            int idLugar = model.getLugar().getId();
            UUID idTrayecto = model.getTrayecto().getCodigo();
            dtos.add(new ParadaDTO(model, idLugar, idTrayecto));
        }
        return dtos;
    }

    public ParadaDTO save(ParadaDTO dtoSended) {
        LugarModel lugar = lugarService.findById(dtoSended.idLugar());
        TrayectoModel trayecto = trayectoService.findById(dtoSended.idTrayecto());
        var model = new ParadaModel(dtoSended);
        model.setLugar(lugar);
        model.setTrayecto(trayecto);

        var modelSave = paradaRepository.save(model);
        return new ParadaDTO(modelSave, lugar.getId(), trayecto.getCodigo());
    }

    public ParadaDTO update(ParadaDTOUpdate dtoSended, Integer id) {
        var model = this.findById(id);
        model.updateValues(dtoSended);

        LugarModel lugar = lugarService.findById(dtoSended.idLugar());
        model.setLugar(lugar);

        var modelUpdated = paradaRepository.save(model);
        UUID idTrayecto = modelUpdated.getTrayecto().getCodigo();
        int idLugar = lugar.getId();
        return new ParadaDTO(modelUpdated, idLugar, idTrayecto);
    }

    public void delete(ParadaModel model) {
        paradaRepository.delete(model);
    }
}
