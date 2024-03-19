package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.precios.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOListBusqueda;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacao;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTO;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOList;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOResponse;
import com.alvaro.empresas.passagens.helpers.ConvertsType;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOList;
import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.CiudadRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class ViajeService {
    @Autowired
    private ViajeRepository viajeRepository;
    @Autowired
    private ParadaRepository paradaRepository;
    @Autowired
    private TrayectoService trayectoService;
    @Autowired
    private PrecioService precioService;
    @Autowired
    private CiudadRepository ciudadRepository;


    public ViajeModel findById(Integer id) {
        var model = viajeRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, ViajeModel.class.getName()));
    }

    public Page<ViajeDTOList> findAll(Pageable pageable) {
        Page<ViajeModel> models = viajeRepository.findAll(pageable);
        return models.map(model -> {
            UUID idTrayecto = model.getTrayecto().getCodigo();
            Integer salida = model.getSalida().getId();
            Integer destino = model.getDestino().getId();
            return new ViajeDTOList(model, idTrayecto, salida, destino);
        });
    }

    //Inconcluso
    public List<ViajeDTOListBusqueda> getViajesFromDia(ViajeDTOSolicitacao dto) {
        // Converter o id da cidade para um LUGAR
        if (dto.idCiudadDestino().equals(dto.idCiudadSalida()))
            throw new ValidationException("idDestino", "El destino no puede ser el mismo que la salida");

        var salidaC = ciudadRepository.findById(dto.idCiudadSalida());
        if (!salidaC.isPresent()) throw new ObjectNotFoundException(dto.idCiudadSalida(), CiudadModel.class.getName());
        var destinoC = ciudadRepository.findById(dto.idCiudadDestino());
        if (!destinoC.isPresent())
            throw new ObjectNotFoundException(dto.idCiudadDestino(), CiudadModel.class.getName());

        LocalDateTime hj = LocalDateTime.now();
        LocalDateTime startDay;
        LocalDateTime endDay = dto.fechaSalida().atTime(LocalTime.MAX);

        List<byte[]> codigosBytes = new ArrayList<>();
        List<ViajeDTOListBusqueda> viajesSelecionados = new ArrayList<>();

        if (hj.toLocalDate().isEqual(dto.fechaSalida())) {
            startDay = hj.plusMinutes(30);
            if (hj.toLocalTime().isAfter(LocalTime.of(23, 30))) return new ArrayList<>();
        } else startDay = dto.fechaSalida().atTime(LocalTime.MIN);


        List<LugarModel> lugaresSalida = salidaC.get().getLugares();
        List<LugarModel> lugaresDestino = destinoC.get().getLugares();
        for (LugarModel lugarSalida : lugaresSalida) {
            codigosBytes = paradaRepository.cargarSalidasDelDia(lugarSalida.getId(), startDay, endDay);

            if (!codigosBytes.isEmpty()) {
                for (byte[] idCodigo : codigosBytes) {
                    UUID codigo = ConvertsType.convertBytesToUUIDHelper(idCodigo);
                    List<ParadaModel> nVezesTrayectoPassaSalida = paradaRepository.nVezesTrayectoPassa(lugarSalida.getId(), codigo);

                    if (nVezesTrayectoPassaSalida.size() != 1) continue;

                    for (LugarModel lugarDestino : lugaresDestino) {
                        List<ParadaModel> nVezesTrayectoPassaDestino = paradaRepository.nVezesTrayectoPassa(lugarDestino.getId(), codigo);
                        if (nVezesTrayectoPassaDestino.size() != 1) continue;

                        String logo = viajeRepository.getLogoEmpresaFromTrayecto(codigo);

                        ParadaModel salida = nVezesTrayectoPassaSalida.get(0);
                        ParadaModel destino = nVezesTrayectoPassaDestino.get(0);
                        List<ViajeModel> viajes = viajeRepository.getFromTrayecto(codigo, salida.getDataHora(), destino.getDataHora());
                        for (ViajeModel viaje : viajes) {
                            ParadaDTOList salidaDTO = convertToParadaDTOList(viaje.getSalida());
                            ParadaDTOList destinoDTO = convertToParadaDTOList(viaje.getDestino());
                            List<PrecioDTO> precios = new ArrayList<>();
                            for (PrecioModel precio : viaje.getPrecios())
                                if (!precio.getLleno()) precios.add(new PrecioDTO(precio));
                            viajesSelecionados.add(new ViajeDTOListBusqueda(viaje, logo, salidaDTO, destinoDTO, precios));
                        }
                    }
                }
            }
        }


        return viajesSelecionados;
    }

    public ViajeDTOListBusqueda getOne(Integer id) {
        var model = this.findById(id);
        UUID codigoTrayecto = model.getTrayecto().getCodigo();

        var salida = convertToParadaDTOList(model.getSalida());
        var destino = convertToParadaDTOList(model.getDestino());

        List<PrecioDTO> precios = new ArrayList<>();
        for (PrecioModel precioModel : model.getPrecios())
            precios.add(new PrecioDTO(precioModel, model.getId()));

        String logo = viajeRepository.getLogoEmpresaFromTrayecto(codigoTrayecto);
        return new ViajeDTOListBusqueda(model, logo, salida, destino, precios);
    }

    @Transactional
    public ViajeDTOResponse save(ViajeDTO dto) {
        var trayecto = trayectoService.findById(dto.idTrayecto());

        if (!trayecto.getViajes().isEmpty()) throw new ValidationException("id", "El trayecto ya posee un viaje");

        //En este punto queda claro que hay minimo dos paradas
        if (trayecto.getParadas().size() < 2)
            throw new ValidationException("paradas", "El trayecto no posee suficientes paradas");

        var salida = trayecto.getMenorParada();
        var destino = trayecto.getMaiorParada();

        if (!destino.getDataHora().isAfter(salida.getDataHora()))
            throw new ValidationException("salida", "La salida posee un horario superior al del destino");

        var model = new ViajeModel();
        model.setTrayecto(trayecto);
        model.setSalida(salida);
        model.setDestino(destino);

        var saved = viajeRepository.save(model);

        //Tratando os precos da viajem
        List<PisoModel> pisos = trayecto.getAutobus().getPisos();

        List<PrecioModel> precios = new ArrayList<>();

        //So podem existir dois pisos
        switch (pisos.size()) {
            case 1 -> precios.add(new PrecioModel(dto.precioPiso1(), 1, pisos.get(0).getNSillas()));
            case 2 -> {
                if (pisos.get(0).getNPiso() == 1) {
                    precios.add(new PrecioModel(dto.precioPiso1(), 1, pisos.get(0).getNSillas()));
                    if (dto.precioPiso2() == null)
                        precios.add(new PrecioModel(dto.precioPiso1(), 2, pisos.get(1).getNSillas()));
                    else precios.add(new PrecioModel(dto.precioPiso2(), 2, pisos.get(1).getNSillas()));
                } else {//Numero piso for 2
                    precios.add(new PrecioModel(dto.precioPiso1(), 1, pisos.get(1).getNSillas()));
                    if (dto.precioPiso2() == null)
                        precios.add(new PrecioModel(dto.precioPiso1(), 2, pisos.get(0).getNSillas()));
                    else precios.add(new PrecioModel(dto.precioPiso2(), 2, pisos.get(0).getNSillas()));
                }
            }
        }
        //Guardando los precios
        List<PrecioDTO> preciosSalvos = precioService.saveAll(precios, saved);
        //Preparando o dto
        var salidaResponse = new ParadaDTO(salida, salida.getLugar().getId(), trayecto.getCodigo());
        var destinoResponse = new ParadaDTO(destino, destino.getLugar().getId(), trayecto.getCodigo());

        return new ViajeDTOResponse(saved, preciosSalvos, trayecto.getCodigo(), salidaResponse, destinoResponse);
    }

    @Transactional
    public void delete(ViajeModel model) {
        for (PrecioModel precio : model.getPrecios())
            if (!precio.getPasajes().isEmpty()) throw new ValidationException("El viaje no pudo ser eliminado");
        viajeRepository.delete(model);
    }

    public ParadaDTOList convertToParadaDTOList(ParadaModel model) {
        return new ParadaDTOList(model, model.getLugar().getNombre(), model.getLugar().getCiudad().getNombre(), model.getLugar().getCiudad().getDepartamento().getNombre());
    }
}