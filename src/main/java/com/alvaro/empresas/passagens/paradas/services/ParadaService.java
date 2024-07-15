package com.alvaro.empresas.passagens.paradas.services;

import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.enums.EnumParada;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOUpdate;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ParadaService {
    private final ParadaRepository paradaRepository;
    private final LugarService lugarService;
    private final ViajeRepository viajeRepository;

    @Autowired
    public ParadaService(
            ParadaRepository paradaRepository,
            LugarService lugarService,
            ViajeRepository viajeRepository
    ) {
        this.paradaRepository = paradaRepository;
        this.lugarService = lugarService;
        this.viajeRepository = viajeRepository;
    }

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

    @Transactional
    public ParadaDTOComplete save(ParadaDTO dtoSended, ViajeModel viaje) {
        LugarModel lugar = lugarService.findById(dtoSended.idLugar());
        if (!lugar.getEnable())
            throw new ValidationException("idLugar", "El lugar no esta disponible");
        //Validacao de Usuario

        var dataParadaAjustada = dtoSended.dataHora().withSecond(0).withNano(0);
        for (ParadaModel parada : viaje.getParadas()) {
            if (parada.getDataHora().isEqual(dataParadaAjustada))
                throw new ValidationException("dataHora", "Ya hay una parada registrada en esta fecha");
            if (parada.getLugar().getId() == dtoSended.idLugar())
                throw new ValidationException("idLugar", "Ya hay una parada registrada que passara por este lugar");
            if (parada.getTipo().equals(EnumParada.DESTINO) && parada.getDataHora().isBefore(LocalDateTime.now()))
                throw new ValidationException("dataHora", "No se puede agregar una parada a un viaje del passado");
        }

        if (!viaje.dataHoraValido(dataParadaAjustada))
            throw new ValidationException("dataHora", "El horario debe estar dentro del intervalo dela salida y del destino");

        var model = new ParadaModel(dtoSended, EnumParada.CAMINO);
        model.setLugar(lugar);
        model.setDataHora(dataParadaAjustada);
        model.setViaje(viaje);
        model.setEmpresa(viaje.getEmpresa());

        var modelSave = paradaRepository.save(model);
        return new ParadaDTOComplete(modelSave, viaje.getCodigo());
    }

    @Transactional
    public ParadaDTOComplete update(ParadaDTOUpdate dtoSended, ParadaModel model) {
        var dataParadaAjustada = dtoSended.dataHora().withSecond(0).withNano(0);
        for (ParadaModel parada : model.getViaje().getParadas()) {
            if (parada.getTipo().equals(EnumParada.SALIDA) && parada.getDataHora().isBefore(LocalDateTime.now()))
                throw new ValidationException("entity", "No se puede editar una parada del passado");
            if (parada.getDataHora().isEqual(dataParadaAjustada) && !parada.getId().equals(model.getId()))
                throw new ValidationException("dataHora", "Ya hay una parada registrada en esta fecha");
            if (parada.getLugar().getId() == dtoSended.idLugar() && !parada.getId().equals(model.getId()))
                throw new ValidationException("idLugar", "Ya hay una parada registrada que passara por este lugar");
        }

        if (model.getTipo().equals(EnumParada.CAMINO))
            if (!model.getViaje().dataHoraValido(dataParadaAjustada))
                throw new ValidationException("dataHora", "La fecha y hora estan fuera del limite");

        if (model.getTipo().equals(EnumParada.SALIDA)) {
            for (ParadaModel parada : model.getViaje().getParadas()) {
                if (!parada.getTipo().equals(EnumParada.SALIDA) && dataParadaAjustada.isAfter(parada.getDataHora()))
                    throw new ValidationException("dataHora", "El horario nuevo dela salida es maior que una del camino");
            }
        }
        if (model.getTipo().equals(EnumParada.DESTINO)) {
            for (ParadaModel parada : model.getViaje().getParadas()) {
                if (!parada.getTipo().equals(EnumParada.DESTINO) && dataParadaAjustada.isBefore(parada.getDataHora()))
                    throw new ValidationException("dataHora", "El horario del destino es menor que una del camino");
            }
        }
        model.updateValues(dtoSended);

        if (dtoSended.idLugar() != null) {
            LugarModel lugar = lugarService.findById(dtoSended.idLugar());
            if (!lugar.getEnable())
                throw new ValidationException("idLugar", "El lugar no esta disponible");
            model.setLugar(lugar);
        }

        if (model.getTipo().equals(EnumParada.SALIDA)) {
            model.getViaje().setDataHoraSalida(dataParadaAjustada);
            viajeRepository.save(model.getViaje());
        }

        var modelUpdated = paradaRepository.save(model);
        UUID idViaje = modelUpdated.getViaje().getCodigo();

        return new ParadaDTOComplete(modelUpdated, idViaje);
    }

    @Transactional
    public void delete(ParadaModel model) {
        paradaRepository.delete(model);
    }
}
