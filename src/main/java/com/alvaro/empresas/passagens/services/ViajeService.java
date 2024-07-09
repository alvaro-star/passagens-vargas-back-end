package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.precios.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOListBusqueda;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacao;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOResponse;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViajeBuscaDTOJPQL;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.PrecioRepository;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class ViajeService {
    private final ViajeRepository viajeRepository;
    private final ParadaRepository paradaRepository;
    private final LugarRepository lugarRepository;
    private final PrecioRepository precioRepository;

    @Autowired
    public ViajeService(ViajeRepository viajeRepository, ParadaRepository paradaRepository, LugarRepository lugarRepository, PrecioRepository precioRepository) {
        this.viajeRepository = viajeRepository;
        this.paradaRepository = paradaRepository;
        this.lugarRepository = lugarRepository;
        this.precioRepository = precioRepository;
    }

    public ViajeModel findById(UUID id) {
        var model = viajeRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, ViajeModel.class.getName()));
    }

    public ViajeDTOResponse getOne(UUID id) {
        var model = this.findById(id);
        Integer idAutobus = model.getAutobus().getId();
        List<ParadaDTOComplete> paradasDTOs = new ArrayList<>();

        for (ParadaModel paradaModel : model.getParadas())
            paradasDTOs.add(new ParadaDTOComplete(paradaModel, model.getCodigo()));

        List<PrecioDTO> precios = new ArrayList<>();
        for (PrecioModel precioModel : model.getPrecios())
            precios.add(new PrecioDTO(precioModel, model.getCodigo()));

        return new ViajeDTOResponse(model, idAutobus, paradasDTOs, precios);
    }

    //Inconcluso
    public List<ViajeDTOListBusqueda> getViajesFromDia(ViajeDTOSolicitacao dto) {
        if (dto.idCiudadDestino().equals(dto.idCiudadSalida()))
            throw new ValidationException("idDestino", "El destino no puede ser el mismo que la salida");

        List<LugarModel> lugaresSalida = lugarRepository.findByCiudadId(dto.idCiudadSalida());
        List<LugarModel> lugaresDestino = lugarRepository.findByCiudadId(dto.idCiudadDestino());

        if (lugaresSalida.isEmpty())
            throw new ObjectNotFoundException(dto.idCiudadSalida(), CiudadModel.class.getName());
        if (lugaresDestino.isEmpty())
            throw new ObjectNotFoundException(dto.idCiudadDestino(), CiudadModel.class.getName());

        LocalDateTime hj = LocalDateTime.now();
        LocalDateTime startDay;
        LocalDateTime endDay = dto.fechaSalida().atTime(LocalTime.MAX);

        List<ViajeDTOListBusqueda> viajesSelecionados = new ArrayList<>();

        if (hj.toLocalDate().isEqual(dto.fechaSalida())) {
            startDay = hj.plusMinutes(30);
            if (hj.toLocalTime().isAfter(LocalTime.of(23, 30))) return new ArrayList<>();
        } else startDay = dto.fechaSalida().atTime(LocalTime.MIN);

        ParadaDTOComplete salidaDTO;
        ParadaDTOComplete destinoDTO;
        List<PrecioDTO> preciosDTOs;
        List<PrecioModel> preciosModels;
        for (LugarModel lugarSalida : lugaresSalida) {
            for (LugarModel lugarDestino : lugaresDestino) {
                List<ViajeBuscaDTOJPQL> salidasDia = paradaRepository.cargarSalidasDelDia(lugarSalida.getId(), lugarDestino.getId(), startDay, endDay);
                for (ViajeBuscaDTOJPQL viajeJPQL : salidasDia) {
                    salidaDTO = new ParadaDTOComplete(viajeJPQL.getSalida(), viajeJPQL.getIdViaje());
                    destinoDTO = new ParadaDTOComplete(viajeJPQL.getDestino(), viajeJPQL.getIdViaje());
                    if (!destinoDTO.dataHora().isAfter(salidaDTO.dataHora())) continue;
                    preciosModels = precioRepository.findByViajeCodigo(viajeJPQL.getIdViaje());
                    preciosDTOs = new ArrayList<>();
                    for (PrecioModel precio : preciosModels)
                        if (!precio.getLleno()) preciosDTOs.add(new PrecioDTO(precio));

                    viajesSelecionados.add(new ViajeDTOListBusqueda(
                            viajeJPQL.getIdViaje(), viajeJPQL.getLogo(), salidaDTO, destinoDTO, preciosDTOs
                    ));
                }
            }
        }
        return viajesSelecionados;
    }

}
       /*
        for (LugarModel lugarSalida : lugaresSalida) {
            List<ParadaModel> salidasDia = paradaRepository.cargarSalidasDelDia(lugarSalida.getId(), startDay, endDay);

            if (!salidasDia.isEmpty()) {
                for (ParadaModel salidaFor : salidasDia) {
                    ViajeModel viaje = salidaFor.getViaje();

                    for (LugarModel lugarDestino : lugaresDestino) {
                        List<ParadaModel> nVezesTrayectoPassaDestino = paradaRepository.findByViajeCodigoAndLugarId(viaje.getCodigo(), lugarDestino.getId());
                        if (nVezesTrayectoPassaDestino.size() != 1) continue;

                    }
                }
            }
        }
*/