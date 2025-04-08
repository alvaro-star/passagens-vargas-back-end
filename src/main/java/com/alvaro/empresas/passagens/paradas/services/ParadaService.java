package com.alvaro.empresas.passagens.paradas.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.PageOutput;
import com.alvaro.empresas.passagens.enums.TipoParada;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.configuracoes.validations.services.ValidEnabledEntities;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaCreateDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaResponseDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaUpdateDTO;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.ViagemRepository;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.services.RepositoryValidationService;
import com.alvaro.empresas.passagens.configuracoes.validations.services.TempoViagemService;

@Service
public class ParadaService {
    @Autowired
    private TempoViagemService tempoViagemService;
    @Autowired
    private ParadaRepository paradaRepository;
    @Autowired
    private LugarRepository lugarRepository;
    @Autowired
    private ViagemRepository viagemRepository;
    @Autowired
    private UserLoguedComponent userLogued;
    @Autowired
    private RepositoryValidationService validationService;

    public ParadaResponseDTO findById(Integer id) {
        var model = paradaRepository.findByIdOrThr(id);
        return new ParadaResponseDTO(model);
    }

    public PageOutput<ParadaResponseDTO> findAll(Pageable pageable) {
        var page = paradaRepository.findAll(pageable);
        var pageDTO = page.map(ParadaResponseDTO::new);
        return new PageOutput<>(pageDTO);
    }

    public PageOutput<ParadaModel> findByLugarId(Integer id, Pageable pageable) {
        var page = paradaRepository.findByLugarId(id, pageable);
        return new PageOutput<>(page);
    }

    @Transactional
    public ParadaResponseDTO save(ParadaCreateDTO dto) {
        var viagem = viagemRepository.findByIdOrThr(dto.idViagem());
        userLogued.validIfIsMyEmpresa(viagem.getEmpresaId());

        ValidEnabledEntities.validEmpresa(viagem.getEmpresa());
        if (validationService.viagemHasPassagem(viagem))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A viagem já possui uma passagem registrada");
        ValidEnabledEntities.validOnibus(viagem.getOnibus());

        LugarModel lugar = lugarRepository.findByIdOrThr(dto.idLugar());
        if (!lugar.getEnabled())
            throw new ValidationException("idLugar", "O lugar não está disponível");

        var model = new ParadaModel(dto, TipoParada.CAMINHO, lugar, viagem, viagem.getEmpresa());

        for (ParadaModel parada : viagem.getParadas()) {
            if (parada.getTipo().equals(TipoParada.SAIDA) && parada.getDataHora().isBefore(LocalDateTime.now()))
                throw new ValidationException("dataHora",
                        "Não é possível adicionar uma parada a uma viagem que já iniciou");
            if (parada.getDataHora().isEqual(model.getDataHora()))
                throw new ValidationException("dataHora", "Já existe uma parada registrada nesta hora");
            if (parada.getLugar().getId().equals(dto.idLugar()))
                throw new ValidationException("idLugar", "Já existe uma parada registrada que passará por este lugar");
        }

        if (!viagem.getSaida().getDataHora().isAfter(model.getDataHora())
                || !viagem.getDestino().getDataHora().isBefore(model.getDataHora()))
            throw new ValidationException("dataHora", "O horário da parada não esta no intervalo da viagem");

        paradaRepository.save(model);
        return new ParadaResponseDTO(model);
    }

    @Transactional
    public ParadaResponseDTO update(Integer id, ParadaUpdateDTO dtoEnviado) {
        var model = paradaRepository.findByIdOrThr(id);
        userLogued.validIfIsMyEmpresa(model.getEmpresaId());
        ValidEnabledEntities.validEmpresa(model.getEmpresa());
        if (validationService.viagemHasPassagem(model.getViagem()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A viagem já possui uma passagem registrada");
        ValidEnabledEntities.validOnibus(model.getViagem().getOnibus());

        var viagem = model.getViagem();
        var dataParadaAjustada = dtoEnviado.dataHora().withSecond(0).withNano(0);
        viagem.getParadas().forEach(parada -> {
            if (parada.getTipo().equals(TipoParada.SAIDA) && parada.getDataHora().isBefore(LocalDateTime.now()))
                throw new ValidationException("dataHora",
                        "Não é possível editar uma parada de uma viagem que já iniciou");
            if (parada.getDataHora().isEqual(dataParadaAjustada) && !parada.getId().equals(model.getId()))
                throw new ValidationException("dataHora", "Já existe uma parada registrada nesta data");
            if (parada.getLugar().getId().equals(dtoEnviado.idLugar()) && !parada.getId().equals(model.getId()))
                throw new ValidationException("idLugar", "Já existe uma parada registrada que passará por este lugar");
        });

        var horarioSaida = viagem.getSaida().getDataHora();
        var horarioDestino = viagem.getDestino().getDataHora();
        switch (model.getTipo()) {
            case SAIDA -> {
                viagem.getParadas().forEach(parada -> {
                    if (!parada.getTipo().equals(TipoParada.SAIDA)
                            && dataParadaAjustada.isAfter(parada.getDataHora()))
                        throw new ValidationException("dataHora",
                                "Uma das paradas possui um horário anterior ao novo horário da SAIDA");
                });
                horarioSaida = dataParadaAjustada;

                viagem.setDataHoraSaida(dataParadaAjustada);
                viagemRepository.save(viagem);
            }
            case CAMINHO -> {
                if (!viagem.getSaida().getDataHora().isAfter(model.getDataHora())
                        || !viagem.getDestino().getDataHora().isBefore(model.getDataHora()))
                    throw new ValidationException("dataHora",
                            "O horário da parada não esta no intervalo da viagem");
            }
            case DESTINO -> {
                viagem.getParadas().forEach(parada -> {
                    if (!parada.getTipo().equals(TipoParada.DESTINO)
                            && dataParadaAjustada.isBefore(parada.getDataHora()))
                        throw new ValidationException("dataHora",
                                "O horário do destino é menor que o de uma parada do caminho");
                });
                horarioDestino = dataParadaAjustada;
            }

        }

        if (!model.getTipo().equals(TipoParada.CAMINHO)) {
            var existe = tempoViagemService.existsViagensActiveFromOnibus(viagem.getOnibus(),
                    horarioSaida,
                    horarioDestino,
                    viagem.getId());
            if (existe)
                throw new ValidationException("dataHora", "O ônibus estara ocupado comn outra viagem neste período");
            try {
                tempoViagemService.validarTempoMaximoViagem(horarioSaida, horarioDestino);
            } catch (Exception e) {
                throw new ValidationException("dataHora", e.getMessage());
            }
        }

        model.updateValues(dtoEnviado);

        if (dtoEnviado.idLugar() != null) {
            LugarModel lugar = lugarRepository.findByIdOrThr(dtoEnviado.idLugar());
            if (!lugar.getEnabled())
                throw new ValidationException("idLugar", "O lugar não está disponível");
            model.setLugar(lugar);
        }

        paradaRepository.save(model);
        return new ParadaResponseDTO(model);
    }

    @Transactional
    public void delete(Integer id) {
        var model = paradaRepository.findByIdOrThr(id);
        if (!userLogued.hasRole(RoleList.ROLE_ADMIN))
            userLogued.validIfIsMyEmpresa(model.getEmpresaId());

        ValidEnabledEntities.validEmpresa(model.getEmpresa());
        ValidEnabledEntities.validOnibus(model.getViagem().getOnibus());

        if (!model.getTipo().equals(TipoParada.CAMINHO))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível excluir a saída ou o destino");

        int indice = -1;
        ParadaModel aux;

        var destinoParada = model.getViagem().getDestino();
        if (destinoParada.getDataHora().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível excluir uma parada de uma viagem do passado");
        for (int i = 0; i < model.getViagem().getParadas().size(); i++) {
            aux = model.getViagem().getParadas().get(i);
            if (aux.getId().equals(model.getId()))
                indice = i;
        }
        if (indice == -1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A parada não está relacionada");
        if (validationService.viagemHasPassagem(model.getViagem()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível eliminar uma parada de uma viagem que já possui um passageiro");

        model.getViagem().getParadas().remove(indice);
        paradaRepository.delete(model);
    }

}