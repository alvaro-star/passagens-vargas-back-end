package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.services.AutobusService;
import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.precios.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.viajes.*;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOForm;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOListBusquedaEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacao;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOEmpresaResponse;
import com.alvaro.empresas.passagens.helpers.beans.MyUserService;
import com.alvaro.empresas.passagens.helpers.beans.UsuarioBean;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class ViajeEmpresaService {
    @Autowired
    private ViajeRepository viajeRepository;
    @Autowired
    private ParadaRepository paradaRepository;
    @Autowired
    private PrecioService precioService;
    @Autowired
    private LugarRepository lugarRepository;
    @Autowired
    private AutobusService autobusService;
    @Autowired
    private MyUserService myUserService;


    public ViajeModel findById(UUID id) {
        var model = viajeRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, ViajeModel.class.getName()));
    }

    public Page<ViajeDTOList> findAll(Pageable pageable) {
        Page<ViajeModel> models = viajeRepository.findAll(pageable);
        return models.map(model -> new ViajeDTOList(model, model.getAutobus().getId()));
    }

    public Page<ViajeDTOList> findAllEmpresa(UUID idEmpresa, Pageable pageable) {
        Page<ViajeModel> models = viajeRepository.findByEmpresaId(idEmpresa, pageable);
        return models.map(model -> new ViajeDTOList(model, model.getAutobus().getId()));
    }

    public List<ViajeDTOListBusquedaEmpresa> getViajesFromDia(ViajeDTOSolicitacao dto) {
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

        List<ViajeDTOListBusquedaEmpresa> viajesSelecionados = new ArrayList<>();

        if (hj.toLocalDate().isEqual(dto.fechaSalida())) {
            startDay = hj.plusMinutes(30);
            if (hj.toLocalTime().isAfter(LocalTime.of(23, 30))) return new ArrayList<>();
        } else startDay = dto.fechaSalida().atTime(LocalTime.MIN);

        UsuarioBean usuarioBean = myUserService.getUser();

        for (LugarModel lugarSalida : lugaresSalida) {
            List<ParadaModel> salidasDia = paradaRepository.cargarSalidasDelDia(lugarSalida.getId(), startDay, endDay);

            if (!salidasDia.isEmpty()) {
                for (ParadaModel salidaFor : salidasDia) {
                    ViajeModel viaje = salidaFor.getViaje();
                    if (viaje.getEmpresa().getId() == usuarioBean.idEmpresa())
                        continue;

                    for (LugarModel lugarDestino : lugaresDestino) {
                        List<ParadaModel> nVezesTrayectoPassaDestino = paradaRepository.nVezesViajePassa(lugarDestino.getId(), viaje.getCodigo());
                        if (nVezesTrayectoPassaDestino.size() != 1) continue;

                        ParadaModel destino = nVezesTrayectoPassaDestino.get(0);

                        ParadaDTOComplete salidaDTO = new ParadaDTOComplete(salidaFor, viaje.getCodigo());
                        ParadaDTOComplete destinoDTO = new ParadaDTOComplete(destino, viaje.getCodigo());

                        List<PrecioDTO> precios = new ArrayList<>();
                        for (PrecioModel precio : viaje.getPrecios())
                            if (!precio.getLleno()) precios.add(new PrecioDTO(precio));

                        viajesSelecionados.add(new ViajeDTOListBusquedaEmpresa(viaje, viaje.getEmpresa().getLogo(), salidaDTO, destinoDTO, precios));
                    }
                }
            }
        }

        return viajesSelecionados;
    }

    public ViajeDTOEmpresaResponse getOne(UUID id) {
        var model = this.findById(id);
        Integer idAutobus = model.getAutobus().getId();
        List<ParadaDTOComplete> paradasDTOs = new ArrayList<>();

        for (ParadaModel paradaModel : model.getParadas())
            paradasDTOs.add(new ParadaDTOComplete(paradaModel, model.getCodigo()));

        List<PrecioDTO> precios = new ArrayList<>();
        for (PrecioModel precioModel : model.getPrecios())
            precios.add(new PrecioDTO(precioModel, model.getCodigo()));

        return new ViajeDTOEmpresaResponse(model, idAutobus, paradasDTOs, precios);
    }

    @Transactional
    public ViajeDTOEmpresaResponse save(ViajeDTOForm dto) {
        var lugarSalida = lugarRepository.findById(dto.salida().idLugar());
        if (lugarSalida.isEmpty())
            throw new ValidationException("salida.idLugar", "El lugarSalida no fue allado");

        var lugarDestino = lugarRepository.findById(dto.destino().idLugar());
        if (lugarDestino.isEmpty())
            throw new ValidationException("destino.idLugar", "El lugarDestino no fue allado");

        if (!dto.destino().dataHora().isAfter(dto.salida().dataHora()))
            throw new ValidationException("salida", "La salida posee un horario superior al del destino");


        var autobus = autobusService.findById(dto.idAutobus());
        var model = new ViajeModel(autobus, autobus.getEmpresa(), new BigDecimal("0.00"), new BigDecimal("0.00"),false);

        var saved = viajeRepository.save(model);

        var salida = new ParadaModel(dto.salida().dataHora(), dto.salida().plataforma(), lugarSalida.get(), saved);
        var destino = new ParadaModel(dto.destino().dataHora(), dto.destino().plataforma(), lugarDestino.get(), saved);

        //Tratando los precios del viaje
        List<PisoModel> pisos = autobus.getPisos();

        List<PrecioModel> precios = new ArrayList<>();

        //Solo pueden existir dos precios
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
        List<ParadaDTOComplete> paradas = new ArrayList<>();
        var salidaSaved = paradaRepository.save(salida);
        paradas.add(new ParadaDTOComplete(salidaSaved, model.getCodigo()));
        var destinoSaved = paradaRepository.save(destino);
        paradas.add(new ParadaDTOComplete(destinoSaved, model.getCodigo()));

        return new ViajeDTOEmpresaResponse(saved, autobus.getId(), paradas, preciosSalvos);
    }

    public ViajeDTOUpdate update(ViajeDTOUpdate dto, UUID id) {//Validacao para que a mudanca seja feita
        //O autobus deve ter o mesmo numero de asientos
        var autobus = autobusService.findById(dto.idAutobus());
        var model = this.findById(id);

        int size = model.getAutobus().getPisos().size();
        if (size != autobus.getPisos().size())
            throw new ValidationException(new FieldMessage("idAutobus", "El autobus no es compatible"));

        if (size == 1) {
            if (model.getAutobus().getPisos().get(0) != autobus.getPisos().get(0)) {
                throw new ValidationException(new FieldMessage("idAutobus", "El autobus no es compatible"));
            }
        } else if (size == 2) {
            if (model.getAutobus().getPisos().get(0) != autobus.getPisos().get(0))
                throw new ValidationException(new FieldMessage("idAutobus", "El autobus no es compatible"));

            if (model.getAutobus().getPisos().get(1) != autobus.getPisos().get(1))
                throw new ValidationException(new FieldMessage("idAutobus", "El autobus no es compatible"));

        } else
            throw new ValidationException(new FieldMessage("idAutobus", "El autobus no es compatible"));

        model.setAutobus(autobus);
        var update = viajeRepository.save(model);
        return new ViajeDTOUpdate(update.getCodigo(), autobus.getId());
    }


    @Transactional
    public void delete(ViajeModel model) {
        for (PrecioModel precio : model.getPrecios())
            if (!precio.getPasajes().isEmpty()) throw new ValidationException("El viaje no pudo ser eliminado");
        viajeRepository.delete(model);
    }
}