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
import com.alvaro.empresas.passagens.services.validacao.TiempoViajeService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ParadaService {
    @Autowired
    private TiempoViajeService tiempoViajeService;
    @Autowired
    private ParadaRepository paradaRepository;
    @Autowired
    private LugarService lugarService;
    @Autowired
    private ViajeRepository viajeRepository;

    public ParadaModel findById(Integer id) {
        var model = paradaRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, ParadaModel.class.getName()));
    }

    public ParadaDTOComplete getOne(Integer id) {
        var model = this.findById(id);
        return new ParadaDTOComplete(model);
    }

    public Page<ParadaDTO> getAll(Pageable pageable) {
        Page<ParadaModel> models = paradaRepository.findAll(pageable);
        return models.map(ParadaDTO::new);
    }

    @Transactional
    public ParadaDTOComplete save(ParadaDTO dtoSended, ViajeModel viaje) {
        LugarModel lugar = lugarService.findById(dtoSended.idLugar());
        if (!lugar.getEnable())
            throw new ValidationException("idLugar", "El lugar no esta disponible");
        //Validacao de Usuario
        var dataParadaAjustada = dtoSended.dataHora().withSecond(0).withNano(0);
        for (ParadaModel parada : viaje.getParadas()) {
            if (parada.getTipo().equals(EnumParada.SALIDA) && parada.getDataHora().isBefore(LocalDateTime.now()))
                throw new ValidationException("dataHora", "No se puede agregar una parada a un viaje que ya inicio");
            if (parada.getDataHora().isEqual(dataParadaAjustada))
                throw new ValidationException("dataHora", "Ya hay una parada registrada en esta esta hora");
            if (parada.getLugar().getId().equals(dtoSended.idLugar()))
                throw new ValidationException("idLugar", "Ya hay una parada registrada que passara por este lugar");
        }

        if (!viaje.dataHoraValido(dataParadaAjustada))
            throw new ValidationException("dataHora", "El horario no es valido");

        var model = new ParadaModel(dtoSended, EnumParada.CAMINO);
        model.setLugar(lugar);
        model.setDataHora(dataParadaAjustada);
        model.setViaje(viaje);
        model.setEmpresa(viaje.getEmpresa());

        paradaRepository.save(model);
        return new ParadaDTOComplete(model);
    }

    @Transactional
    public ParadaDTOComplete update(ParadaDTOUpdate dtoSended, ParadaModel model) {
        var dataParadaAjustada = dtoSended.dataHora().withSecond(0).withNano(0);
        for (ParadaModel parada : model.getViaje().getParadas()) {
            if (parada.getTipo().equals(EnumParada.SALIDA) && parada.getDataHora().isBefore(LocalDateTime.now()))
                throw new ValidationException("dataHora", "No se puede editar una parada de un viaje que ya inicio");
            if (parada.getDataHora().isEqual(dataParadaAjustada) && !parada.getId().equals(model.getId()))
                throw new ValidationException("dataHora", "Ya hay una parada registrada en esta fecha");
            if (parada.getLugar().getId().equals(dtoSended.idLugar()) && !parada.getId().equals(model.getId()))
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

        boolean valido = true;
        if (!model.getTipo().equals(EnumParada.CAMINO)) {
            valido = validarHorarioParadaExterno(model, dataParadaAjustada);
        }
        if (!valido)
            throw new ValidationException("dataHora", "El autobus esta ocupado en esta hora");

        paradaRepository.save(model);
        return new ParadaDTOComplete(model);
    }

    private boolean validarHorarioParadaExterno(ParadaModel modelEscolhido, LocalDateTime novoDataHoraAjustada) {
        var existe = true;
        var valido = false;

        if (modelEscolhido.getTipo().equals(EnumParada.SALIDA)) {
            existe = tiempoViajeService.existsViajesActiveFromAutobus(
                    modelEscolhido.getViaje().getAutobus(),
                    novoDataHoraAjustada,
                    modelEscolhido.getViaje().getDestino().getDataHora(),
                    modelEscolhido.getViaje().getCodigo()
            );
            valido = tiempoViajeService.
                    validarTempoMaximoViaje(novoDataHoraAjustada, modelEscolhido.getViaje().getDestino().getDataHora());
        } else if (modelEscolhido.getTipo().equals(EnumParada.DESTINO)) {
            existe = tiempoViajeService.existsViajesActiveFromAutobus(
                    modelEscolhido.getViaje().getAutobus(),
                    modelEscolhido.getViaje().getSalida().getDataHora(),
                    novoDataHoraAjustada,
                    modelEscolhido.getViaje().getCodigo()
            );
            valido = tiempoViajeService.validarTempoMaximoViaje(modelEscolhido.getViaje().getSalida().getDataHora(), novoDataHoraAjustada);
        }

        return !existe && valido;
    }

    @Transactional
    public void delete(ParadaModel model) {
        paradaRepository.delete(model);
    }
}
