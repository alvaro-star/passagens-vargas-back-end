package com.alvaro.empresas.passagens.services;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.precos.PrecoDTO;
import com.alvaro.empresas.passagens.dtos.viagens.ViagemDTOResponse;
import com.alvaro.empresas.passagens.dtos.viagens.busca.ViagemDTOListBusca;
import com.alvaro.empresas.passagens.dtos.viagens.busca.ViagemDTOSolicitacao;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViagemBuscaDTOJPQL;
import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.repositories.PrecoRepository;
import com.alvaro.empresas.passagens.repositories.ViagemRepository;


@Service
public class ViagemService {
    @Autowired
    private ViagemRepository viagemRepository;
    @Autowired
    private LugarRepository lugarRepository;
    @Autowired
    private PrecoRepository precoRepository;

    public ViagemModel findById(UUID id) {
        var model = viagemRepository.findById(id);
        return model.orElseThrow(() -> new EntityNotFoundException(id, ViagemModel.class));
    }

    public ViagemDTOResponse getOne(UUID id) {
        var model = this.findById(id);
        var paradas = model.getParadas().stream().map(ParadaDTOComplete::new).toList();
        var precos = model.getPrecos().stream().map(PrecoDTO::new).toList();
        return new ViagemDTOResponse(model, paradas, precos);
    }

    //Inconcluso
    public List<ViagemDTOListBusca> getViagensFromDia(ViagemDTOSolicitacao dto) {
        if (dto.idCidadeDestino().equals(dto.idCidadeSaida()))
            throw new ValidationException("idDestino", "O destino não pode ser o mesmo que a saida");

        List<LugarModel> lugaresSaida = lugarRepository.findByCidadeId(dto.idCidadeSaida());
        List<LugarModel> lugaresDestino = lugarRepository.findByCidadeId(dto.idCidadeDestino());

        if (lugaresSaida.isEmpty())
            throw new EntityNotFoundException(dto.idCidadeSaida(), CidadeModel.class);
        if (lugaresDestino.isEmpty())
            throw new EntityNotFoundException(dto.idCidadeDestino(), CidadeModel.class);

        LocalDateTime hj = LocalDateTime.now();
        LocalDateTime startDay;
        LocalDateTime endDay = dto.dataSaida().atTime(LocalTime.MAX);

        List<ViagemDTOListBusca> viagensSelecionados = new ArrayList<>();

        if (hj.toLocalDate().isEqual(dto.dataSaida())) {
            startDay = hj.plusMinutes(30);
            if (hj.toLocalTime().isAfter(LocalTime.of(23, 30))) return new ArrayList<>();
        } else startDay = dto.dataSaida().atTime(LocalTime.MIN);

        for (LugarModel lugarSaida : lugaresSaida) {
            for (LugarModel lugarDestino : lugaresDestino) {
                List<ViagemBuscaDTOJPQL> viagens = viagemRepository.findByStartInInterval(lugarSaida.getId(), lugarDestino.getId(), startDay, endDay);
                for (ViagemBuscaDTOJPQL viagem : viagens) {
                    var saidaDTO = new ParadaDTOComplete(viagem.saida());
                    var destinoDTO = new ParadaDTOComplete(viagem.destino());
                    if (!destinoDTO.dataHora().isAfter(saidaDTO.dataHora())) continue;
                    var precosModels = precoRepository.findByViagemId(viagem.idViagem());
                    var precosDTOs = precosModels.stream().map(PrecoDTO::new).toList();

                    viagensSelecionados.add(new ViagemDTOListBusca(viagem, saidaDTO, destinoDTO, precosDTOs));
                }
            }
        }
        return viagensSelecionados;
    }
}