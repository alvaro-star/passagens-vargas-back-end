package com.alvaro.empresas.passagens.paradas.services;


import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.enums.TipoParada;
import com.alvaro.empresas.passagens.helpers.validators.ValidEnabledEntities;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOUpdate;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.ViagemRepository;
import com.alvaro.empresas.passagens.services.validacao.TempoViagemService;
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
    private TempoViagemService tempoViagemService;
    @Autowired
    private ParadaRepository paradaRepository;
    @Autowired
    private LugarService lugarService;
    @Autowired
    private ViagemRepository viagemRepository;

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
    public ParadaDTOComplete save(ParadaDTO dtoEnviado, ViagemModel viagem) {
        ValidEnabledEntities.validEmpresa(viagem.getEmpresa());
        ValidEnabledEntities.validOnibus(viagem.getOnibus());

        LugarModel lugar = lugarService.findById(dtoEnviado.idLugar());
        if (!lugar.getHabilitado())
            throw new ValidationException("idLugar", "O lugar não está disponível");
        //Validação de Usuário
        var dataParadaAjustada = dtoEnviado.dataHora().withSecond(0).withNano(0);
        for (ParadaModel parada : viagem.getParadas()) {
            if (parada.getTipo().equals(TipoParada.SAIDA) && parada.getDataHora().isBefore(LocalDateTime.now()))
                throw new ValidationException("dataHora", "Não é possível adicionar uma parada a uma viagem que já iniciou");
            if (parada.getDataHora().isEqual(dataParadaAjustada))
                throw new ValidationException("dataHora", "Já existe uma parada registrada nesta hora");
            if (parada.getLugar().getId().equals(dtoEnviado.idLugar()))
                throw new ValidationException("idLugar", "Já existe uma parada registrada que passará por este lugar");
        }

        if (!viagem.dataHoraValido(dataParadaAjustada))
            throw new ValidationException("dataHora", "O horário não é válido");

        var model = new ParadaModel(dtoEnviado, TipoParada.CAMINO);
        model.setLugar(lugar);
        model.setDataHora(dataParadaAjustada);
        model.setViagem(viagem);
        model.setEmpresa(viagem.getEmpresa());

        paradaRepository.save(model);
        return new ParadaDTOComplete(model);
    }

    @Transactional
    public ParadaDTOComplete update(ParadaDTOUpdate dtoEnviado, ParadaModel model) {
        ValidEnabledEntities.validEmpresa(model.getEmpresa());
        ValidEnabledEntities.validOnibus(model.getViagem().getOnibus());

        var dataParadaAjustada = dtoEnviado.dataHora().withSecond(0).withNano(0);
        for (ParadaModel parada : model.getViagem().getParadas()) {
            if (parada.getTipo().equals(TipoParada.SAIDA) && parada.getDataHora().isBefore(LocalDateTime.now()))
                throw new ValidationException("dataHora", "Não é possível editar uma parada de uma viagem que já iniciou");
            if (parada.getDataHora().isEqual(dataParadaAjustada) && !parada.getId().equals(model.getId()))
                throw new ValidationException("dataHora", "Já existe uma parada registrada nesta data");
            if (parada.getLugar().getId().equals(dtoEnviado.idLugar()) && !parada.getId().equals(model.getId()))
                throw new ValidationException("idLugar", "Já existe uma parada registrada que passará por este lugar");
        }

        if (model.getTipo().equals(TipoParada.CAMINO))
            if (!model.getViagem().dataHoraValido(dataParadaAjustada))
                throw new ValidationException("dataHora", "A data e hora estão fora do limite");

        if (model.getTipo().equals(TipoParada.SAIDA)) {
            for (ParadaModel parada : model.getViagem().getParadas()) {
                if (!parada.getTipo().equals(TipoParada.SAIDA) && dataParadaAjustada.isAfter(parada.getDataHora()))
                    throw new ValidationException("dataHora", "O novo horário da saída é maior que o de uma parada do caminho");
            }
        }
        if (model.getTipo().equals(TipoParada.DESTINO)) {
            for (ParadaModel parada : model.getViagem().getParadas()) {
                if (!parada.getTipo().equals(TipoParada.DESTINO) && dataParadaAjustada.isBefore(parada.getDataHora()))
                    throw new ValidationException("dataHora", "O horário do destino é menor que o de uma parada do caminho");
            }
        }
        model.updateValues(dtoEnviado);

        if (dtoEnviado.idLugar() != null) {
            LugarModel lugar = lugarService.findById(dtoEnviado.idLugar());
            if (!lugar.getHabilitado())
                throw new ValidationException("idLugar", "O lugar não está disponível");
            model.setLugar(lugar);
        }

        if (model.getTipo().equals(TipoParada.SAIDA)) {
            model.getViagem().setDataHoraSaida(dataParadaAjustada);
            viagemRepository.save(model.getViagem());
        }

        boolean valido = true;
        if (!model.getTipo().equals(TipoParada.CAMINO)) {
            valido = validarHorarioParadaExterno(model, dataParadaAjustada);
        }
        if (!valido)
            throw new ValidationException("dataHora", "O ônibus está ocupado nesta hora");

        paradaRepository.save(model);
        return new ParadaDTOComplete(model);
    }

    private boolean validarHorarioParadaExterno(ParadaModel modelEscolhido, LocalDateTime novoDataHoraAjustada) {
        var existe = true;
        var valido = false;

        if (modelEscolhido.getTipo().equals(TipoParada.SAIDA)) {
            existe = tempoViagemService.existsViagensActiveFromOnibus(
                    modelEscolhido.getViagem().getOnibus(),
                    novoDataHoraAjustada,
                    modelEscolhido.getViagem().getDestino().getDataHora(),
                    modelEscolhido.getViagem().getId()
            );
            valido = tempoViagemService.
                    validarTempoMaximoViagem(novoDataHoraAjustada, modelEscolhido.getViagem().getDestino().getDataHora());
        } else if (modelEscolhido.getTipo().equals(TipoParada.DESTINO)) {
            existe = tempoViagemService.existsViagensActiveFromOnibus(
                    modelEscolhido.getViagem().getOnibus(),
                    modelEscolhido.getViagem().getSaida().getDataHora(),
                    novoDataHoraAjustada,
                    modelEscolhido.getViagem().getId()
            );
            valido = tempoViagemService.validarTempoMaximoViagem(modelEscolhido.getViagem().getSaida().getDataHora(), novoDataHoraAjustada);
        }

        return !existe && valido;
    }

    @Transactional
    public void delete(ParadaModel model) {
        paradaRepository.delete(model);
    }
}