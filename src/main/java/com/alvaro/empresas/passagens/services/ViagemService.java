package com.alvaro.empresas.passagens.services;


import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.EntityNotFoundException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.precos.PrecoResponseDTO;
import com.alvaro.empresas.passagens.dtos.viagens.JPQL.ViagemWithLogoDTOJPQL;
import com.alvaro.empresas.passagens.dtos.viagens.buyer.ViagemResponseDTO;
import com.alvaro.empresas.passagens.dtos.viagens.ViagemSolicitacaoDTO;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaResponseDTO;
import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.repositories.PrecoRepository;
import com.alvaro.empresas.passagens.repositories.ViagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class ViagemService {
    @Autowired
    private ViagemRepository viagemRepository;
    @Autowired
    private LugarRepository lugarRepository;
    @Autowired
    private PrecoRepository precoRepository;

    public ViagemResponseDTO findById(UUID id) {
        var optionalModel = viagemRepository.findByIdWithLogo(id)
                .orElseThrow(() -> new EntityNotFoundException(id, ViagemModel.class));
        var paradas = optionalModel.viagem().getParadas().stream().map(ParadaResponseDTO::new).toList();
        var precos = optionalModel.viagem().getPrecos().stream().map(PrecoResponseDTO::new).toList();
        return new ViagemResponseDTO(optionalModel.viagem(), optionalModel.logo(), paradas, precos);
    }

    //Inconcluso
    public List<ViagemResponseDTO> getViagensFromDia(ViagemSolicitacaoDTO dto) {
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

        List<ViagemResponseDTO> viagensSelecionados = new ArrayList<>();

        if (hj.toLocalDate().isEqual(dto.dataSaida())) {
            startDay = hj.plusMinutes(30);
            if (hj.toLocalTime().isAfter(LocalTime.of(23, 30))) return new ArrayList<>();
        } else startDay = dto.dataSaida().atTime(LocalTime.MIN);

        for (LugarModel lugarSaida : lugaresSaida) {
            for (LugarModel lugarDestino : lugaresDestino) {
                List<ViagemWithLogoDTOJPQL> viagens = viagemRepository.findByStartInInterval(lugarSaida.getId(), lugarDestino.getId(), startDay, endDay);
                for (ViagemWithLogoDTOJPQL viagem : viagens) {
                    List<ParadaResponseDTO> paradasDTO = new ArrayList<>();
                    paradasDTO.add(new ParadaResponseDTO(viagem.saida()));
                    paradasDTO.add(new ParadaResponseDTO(viagem.destino()));

                    var precosDTOs = viagem.viagem().getPrecos().stream().map(PrecoResponseDTO::new).toList();

                    viagensSelecionados.add(new ViagemResponseDTO(viagem.viagem(), viagem.logo(), paradasDTO, precosDTOs));
                }
            }
        }
        return viagensSelecionados;
    }
}