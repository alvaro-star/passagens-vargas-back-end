package com.alvaro.empresas.passagens.services;


import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.precos.PrecoDTO;
import com.alvaro.empresas.passagens.dtos.viagens.busca.ViagemDTOListBusca;
import com.alvaro.empresas.passagens.dtos.viagens.busca.ViagemDTOSolicitacao;
import com.alvaro.empresas.passagens.dtos.viagens.ViagemDTOResponse;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViagemBuscaDTOJPQL;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.repositories.PrecoRepository;
import com.alvaro.empresas.passagens.repositories.ViagemRepository;
import org.hibernate.ObjectNotFoundException;
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

    public ViagemModel findById(UUID id) {
        var model = viagemRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, ViagemModel.class.getName()));
    }

    public ViagemDTOResponse getOne(UUID id) {
        var model = this.findById(id);

        List<ParadaDTOComplete> paradasDTOs = new ArrayList<>();
        for (ParadaModel paradaModel : model.getParadas())
            paradasDTOs.add(new ParadaDTOComplete(paradaModel));

        List<PrecoDTO> precios = new ArrayList<>();
        for (PrecoModel precoModel : model.getPrecos())
            precios.add(new PrecoDTO(precoModel));

        return new ViagemDTOResponse(model, paradasDTOs, precios);
    }

    //Inconcluso
    public List<ViagemDTOListBusca> getViajesFromDia(ViagemDTOSolicitacao dto) {
        if (dto.idCiudadDestino().equals(dto.idCiudadSalida()))
            throw new ValidationException("idDestino", "El destino no puede ser el mismo que la salida");

        List<LugarModel> lugaresSalida = lugarRepository.findByCiudadId(dto.idCiudadSalida());
        List<LugarModel> lugaresDestino = lugarRepository.findByCiudadId(dto.idCiudadDestino());

        if (lugaresSalida.isEmpty())
            throw new ObjectNotFoundException(dto.idCiudadSalida(), CidadeModel.class.getName());
        if (lugaresDestino.isEmpty())
            throw new ObjectNotFoundException(dto.idCiudadDestino(), CidadeModel.class.getName());

        LocalDateTime hj = LocalDateTime.now();
        LocalDateTime startDay;
        LocalDateTime endDay = dto.fechaSalida().atTime(LocalTime.MAX);

        List<ViagemDTOListBusca> viajesSelecionados = new ArrayList<>();

        if (hj.toLocalDate().isEqual(dto.fechaSalida())) {
            startDay = hj.plusMinutes(30);
            if (hj.toLocalTime().isAfter(LocalTime.of(23, 30))) return new ArrayList<>();
        } else startDay = dto.fechaSalida().atTime(LocalTime.MIN);

        ParadaDTOComplete salidaDTO;
        ParadaDTOComplete destinoDTO;
        List<PrecoDTO> preciosDTOs;
        List<PrecoModel> preciosModels;
        for (LugarModel lugarSalida : lugaresSalida) {
            for (LugarModel lugarDestino : lugaresDestino) {
                List<ViagemBuscaDTOJPQL> salidasDia = viagemRepository.findByStartInInterval(lugarSalida.getId(), lugarDestino.getId(), startDay, endDay);
                for (ViagemBuscaDTOJPQL viajeJPQL : salidasDia) {
                    salidaDTO = new ParadaDTOComplete(viajeJPQL.saida());
                    destinoDTO = new ParadaDTOComplete(viajeJPQL.destino());
                    if (!destinoDTO.dataHora().isAfter(salidaDTO.dataHora())) continue;
                    preciosModels = precoRepository.findByViagemId(viajeJPQL.idViagem());
                    preciosDTOs = new ArrayList<>();
                    for (PrecoModel precio : preciosModels)
                        if (!precio.getCheio()) preciosDTOs.add(new PrecoDTO(precio));

                    viajesSelecionados.add(new ViagemDTOListBusca(
                            viajeJPQL.idViagem(), viajeJPQL.logo(), salidaDTO, destinoDTO, preciosDTOs
                    ));
                }
            }
        }
        return viajesSelecionados;
    }
}