package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.configurations.exceptions.InternalException.BadRequestException;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.precios.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacaoEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacaoFromAutobus;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacaoFromEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOForm;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOFormCopy;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOListBusquedaEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.JPQL.ViajeDTOJPQL;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOUpdate;
import com.alvaro.empresas.passagens.enums.EnumParada;
import com.alvaro.empresas.passagens.helpers.DateAuxiliarFunctions;
import com.alvaro.empresas.passagens.helpers.thymeleaf.PDFThymeleaf;
import com.alvaro.empresas.passagens.helpers.thymeleaf.PasajeItemListTHModel;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.PasajeModel;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViajeEmpresaDTOJPQ;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.PrecioRepository;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import com.alvaro.empresas.passagens.services.validacao.TiempoViajeService;
import com.itextpdf.kernel.geom.PageSize;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


@Service
public class ViajeEmpresaService {
    private final TiempoViajeService tiempoViajeService;
    private final ViajeRepository viajeRepository;
    private final ParadaRepository paradaRepository;
    private final PrecioService precioService;
    private final LugarRepository lugarRepository;
    private final DateAuxiliarFunctions helperDate;
    private final PrecioRepository precioRepository;
    private final PDFThymeleaf pdfThymeleaf;

    @Autowired
    public ViajeEmpresaService(
            ViajeRepository viajeRepository,
            TiempoViajeService tiempoViajeService,
            ParadaRepository paradaRepository,
            PrecioService precioService,
            LugarRepository lugarRepository,
            DateAuxiliarFunctions helperDate,
            PrecioRepository precioRepository,
            PDFThymeleaf pdfThymeleaf) {
        this.viajeRepository = viajeRepository;
        this.paradaRepository = paradaRepository;
        this.tiempoViajeService = tiempoViajeService;
        this.precioService = precioService;
        this.lugarRepository = lugarRepository;
        this.helperDate = helperDate;
        this.precioRepository = precioRepository;
        this.pdfThymeleaf = pdfThymeleaf;
    }

    public ViajeModel findById(UUID id) {
        var model = viajeRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, ViajeModel.class.getName()));
    }

    public Page<ViajeDTOListBusquedaEmpresa> findAllFromEmpresaBetweenDates(EmpresaModel empresa, ViajeDTOSolicitacaoFromEmpresa solicitacao, Pageable pageable) {
        Page<ViajeDTOJPQL> models;
        LocalDateTime dataInicio = helperDate.getDateWithFirstDayOfMonth(solicitacao.dataAnalise());
        LocalDateTime dataFim = helperDate.getDateWithLastDayOfMonth(solicitacao.dataAnalise());

        models = viajeRepository.findByEmpresaIdInInterval(empresa.getId(), dataInicio, dataFim, pageable);

        return models.map(model -> {
            if (model.salida() == null || model.destino() == null)
                throw new BadRequestException("Hay un viaje que no posse ninguna parada");
            ParadaDTOComplete salidaDTO = new ParadaDTOComplete(model.salida(), model.viaje().getCodigo());
            ParadaDTOComplete destinoDTO = new ParadaDTOComplete(model.destino(), model.viaje().getCodigo());
            return new ViajeDTOListBusquedaEmpresa(model.viaje(), "", salidaDTO, destinoDTO, new ArrayList<>());
        });
    }

    public Page<ViajeDTOListBusquedaEmpresa> findAllFromAutobus(AutobusModel autobusModel, ViajeDTOSolicitacaoFromAutobus solicitacao, Pageable pageable) {
        Page<ViajeDTOJPQL> models;
        LocalDateTime dataInicio = helperDate.getDateWithFirstDayOfMonth(solicitacao.dataAnalise());
        LocalDateTime dataFim = helperDate.getDateWithLastDayOfMonth(solicitacao.dataAnalise());

        models = viajeRepository.findByEmpresaIdAndAutobusId(autobusModel.getEmpresa().getId(), autobusModel.getId(), dataInicio, dataFim, pageable);

        return models.map(model -> {
            if (model.salida() == null || model.destino() == null)
                throw new ValidationException("lista", "Hay un viaje que no posse ninguna parada");
            ParadaDTOComplete salidaDTO = new ParadaDTOComplete(model.salida(), model.viaje().getCodigo());
            ParadaDTOComplete destinoDTO = new ParadaDTOComplete(model.destino(), model.viaje().getCodigo());
            return new ViajeDTOListBusquedaEmpresa(model.viaje(), "", salidaDTO, destinoDTO, new ArrayList<>());
        });
    }


    public List<ViajeDTOListBusquedaEmpresa> getViajesFromDia(UUID idEmpresa, ViajeDTOSolicitacaoEmpresa dto) {
        if (dto.idCiudadDestino().equals(dto.idCiudadSalida()))
            throw new ValidationException("idDestino", "El destino no puede ser el mismo que la salida");

        List<LugarModel> lugaresSalida = lugarRepository.findByCiudadId(dto.idCiudadSalida());
        List<LugarModel> lugaresDestino = lugarRepository.findByCiudadId(dto.idCiudadDestino());

        if (lugaresSalida.isEmpty())
            throw new ObjectNotFoundException(dto.idCiudadSalida(), CiudadModel.class.getName());

        if (lugaresDestino.isEmpty())
            throw new ObjectNotFoundException(dto.idCiudadDestino(), CiudadModel.class.getName());

        LocalDateTime startDay = dto.fechaSalida().atTime(LocalTime.MIN);
        LocalDateTime endDay = dto.fechaSalida().atTime(LocalTime.MAX);

        List<ViajeDTOListBusquedaEmpresa> viajesSelecionados = new ArrayList<>();
        ParadaDTOComplete salidaDTO;
        ParadaDTOComplete destinoDTO;
        List<PrecioDTO> precios;

        for (LugarModel lugarSalida : lugaresSalida) {
            for (LugarModel lugarDestino : lugaresDestino) {
                List<ViajeEmpresaDTOJPQ> salidasDia = paradaRepository.cargarSalidasDelDiaFromEmpresa(idEmpresa, lugarSalida.getId(), lugarDestino.getId(), startDay, endDay);
                for (ViajeEmpresaDTOJPQ ViajeEmpresaDTOJPQ : salidasDia) {
                    salidaDTO = new ParadaDTOComplete(ViajeEmpresaDTOJPQ.getSalida(), ViajeEmpresaDTOJPQ.getViaje().getCodigo());
                    destinoDTO = new ParadaDTOComplete(ViajeEmpresaDTOJPQ.getDestino(), ViajeEmpresaDTOJPQ.getViaje().getCodigo());
                    if (!destinoDTO.dataHora().isAfter(salidaDTO.dataHora())) continue;

                    precios = new ArrayList<>();
                    for (PrecioModel precio : ViajeEmpresaDTOJPQ.getViaje().getPrecios())
                        if (!precio.getLleno()) precios.add(new PrecioDTO(precio));
                    viajesSelecionados.add(new ViajeDTOListBusquedaEmpresa(ViajeEmpresaDTOJPQ.getViaje(), null, salidaDTO, destinoDTO, precios));
                }
            }
        }

        return viajesSelecionados;
    }

    public List<ViajeDTOListBusquedaEmpresa> getViajesFromSalida(UUID idEmpresa, ViajeDTOSolicitacaoEmpresa dto) {
        List<LugarModel> lugaresSalida = lugarRepository.findByCiudadId(dto.idCiudadSalida());
        if (lugaresSalida.isEmpty())
            throw new ObjectNotFoundException(dto.idCiudadSalida(), CiudadModel.class.getName());

        LocalDateTime startDay = dto.fechaSalida().atTime(LocalTime.MIN);
        LocalDateTime endDay = dto.fechaSalida().atTime(LocalTime.MAX);

        List<ViajeDTOListBusquedaEmpresa> viajesSelecionados = new ArrayList<>();
        ParadaDTOComplete salidaDTO;
        ParadaDTOComplete destinoDTO;
        List<PrecioDTO> precios;

        for (LugarModel lugarSalida : lugaresSalida) {
            List<ViajeEmpresaDTOJPQ> salidasDia = paradaRepository.cargarSalidasDelDiaFromEmpresa2(idEmpresa, lugarSalida.getId(), startDay, endDay);
            for (ViajeEmpresaDTOJPQ ViajeEmpresaDTOJPQ : salidasDia) {
                salidaDTO = new ParadaDTOComplete(ViajeEmpresaDTOJPQ.getSalida(), ViajeEmpresaDTOJPQ.getViaje().getCodigo());
                destinoDTO = new ParadaDTOComplete(ViajeEmpresaDTOJPQ.getDestino(), ViajeEmpresaDTOJPQ.getViaje().getCodigo());

                if (!destinoDTO.dataHora().isAfter(salidaDTO.dataHora())) continue;
                precios = new ArrayList<>();
                for (PrecioModel precio : ViajeEmpresaDTOJPQ.getViaje().getPrecios())
                    precios.add(new PrecioDTO(precio));
                viajesSelecionados.add(new ViajeDTOListBusquedaEmpresa(ViajeEmpresaDTOJPQ.getViaje(), null, salidaDTO, destinoDTO, precios));
            }
        }

        return viajesSelecionados;
    }

    public byte[] getPdfFromViaje(UUID id) {
        var model = this.findById(id);
        List<PasajeModel> pasajes = new ArrayList<>();
        for (PrecioModel precio : model.getPrecios())
            pasajes.addAll(precio.getPasajes());

        List<PasajeItemListTHModel> pasajesTh = pasajes.stream().map(PasajeItemListTHModel::new).toList();
        Context context = new Context();
        context.setVariable("pasajes", pasajesTh);
        PageSize pageSize = new PageSize(PageSize.A4.getHeight(), PageSize.A4.getWidth());
        return pdfThymeleaf.generatePDFByTemplate("/empresa/pasajerosList", context, pageSize);
    }

    @Transactional
    public ViajeDTOEmpresaResponse save(ViajeDTOForm dto, AutobusModel autobus) {
        var lugarSalida = lugarRepository.findById(dto.salida().idLugar());
        if (lugarSalida.isEmpty()) throw new ValidationException("salida.idLugar", "El lugarSalida no fue allado");

        var lugarDestino = lugarRepository.findById(dto.destino().idLugar());
        if (lugarDestino.isEmpty()) throw new ValidationException("destino.idLugar", "El lugarDestino no fue allado");

        LocalDateTime dataHoraSalidaAjustada = dto.salida().dataHora().withSecond(0).withNano(0);
        LocalDateTime dataHoraDestinoAjustada = dto.destino().dataHora().withSecond(0).withNano(0);

        if (!dataHoraDestinoAjustada.isAfter(dataHoraSalidaAjustada))
            throw new ValidationException("salida", "La salida posee un horario superior al del destino");
        if (!tiempoViajeService.validarTempoMaximoViaje(dataHoraSalidaAjustada, dataHoraDestinoAjustada))
            throw new ValidationException("destino.dataHora", "Un viaje puede durar maximo 3 dias");

        boolean viajeInIntervalo = tiempoViajeService.existsViajesActiveFromAutobus(autobus.getEmpresa().getId(), autobus.getId(), dataHoraSalidaAjustada, dataHoraDestinoAjustada);
        if (viajeInIntervalo)
            throw new ValidationException("destino.dataHora", "Existe un viaje del autobus que ocurre en este intervalo");

        //Adicionando la hora de salida, -> nota: es conveniencia
        var model = new ViajeModel(autobus, autobus.getEmpresa(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false, dataHoraSalidaAjustada);
        var saved = viajeRepository.save(model);

        var salida = new ParadaModel(dataHoraSalidaAjustada, dto.salida().plataforma(), EnumParada.SALIDA, lugarSalida.get(), saved, saved.getEmpresa());
        var destino = new ParadaModel(dataHoraDestinoAjustada, dto.destino().plataforma(), EnumParada.DESTINO, lugarDestino.get(), saved, saved.getEmpresa());

        //Tratando los precios del viaje
        List<PisoModel> pisos = autobus.getPisos();

        List<PrecioModel> precios = new ArrayList<>();
        List<BigDecimal> preciodDto = List.of(dto.precioPiso1(), dto.precioPiso2());
        PisoModel aux;
        for (int i = 1; i <= autobus.getPisos().size(); i++) {
            aux = autobus.getPisoByNumero(i);
            BigDecimal precioItemList = preciodDto.get(i - 1);
            if (precioItemList == null)
                precioItemList = preciodDto.get(i - 2);
            precios.add(new PrecioModel(precioItemList, i, aux.getNSillas()));
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

    @Transactional
    public int saveOneCopy(ViajeDTOFormCopy dto, ViajeModel viaje) {
        long diffDias;
        LocalDateTime dataViajeOriginal = viaje.getDataHoraSalida();

        if (dto.dataNovo().isEqual(viaje.getDataHoraSalida().toLocalDate()))
            throw new ValidationException("dataNovo", "La nueva fecha es identico al del viaje");

        LocalDateTime firsHourViaje = viaje.getSalida().getDataHora();
        LocalDateTime lastHourViaje = viaje.getDestino().getDataHora();

        diffDias = ChronoUnit.DAYS.between(firsHourViaje.toLocalDate(), lastHourViaje.toLocalDate());

        firsHourViaje = dto.dataNovo().atTime(
                firsHourViaje.getHour(),
                firsHourViaje.getMinute(),
                firsHourViaje.getSecond(),
                firsHourViaje.getNano()
        );

        lastHourViaje = dto.dataNovo().atTime(
                lastHourViaje.getHour(),
                lastHourViaje.getMinute(),
                lastHourViaje.getSecond(),
                lastHourViaje.getNano()
        );


        diffDias = (diffDias < 0) ? -diffDias : diffDias;
        if (diffDias > 0)
            lastHourViaje = lastHourViaje.plusDays(diffDias);
        boolean existe = tiempoViajeService.existsViajesActiveFromAutobus(
                viaje.getEmpresa().getId(), viaje.getAutobus().getId(), firsHourViaje, lastHourViaje);
        if (existe) return -1;

        diffDias = ChronoUnit.DAYS.between(dataViajeOriginal.toLocalDate(), firsHourViaje.toLocalDate());
        diffDias = (diffDias < 0) ? -diffDias : diffDias;

        List<ParadaModel> paradaModelSave = new ArrayList<>();
        List<PrecioModel> preciosModelSave = new ArrayList<>();
        var viajeNew = new ViajeModel(viaje.getDataHoraSalida().plusDays(diffDias), viaje.getAutobus(), viaje.getEmpresa());

        ParadaModel auxParada;
        for (ParadaModel aux : viaje.getParadas()) {
            auxParada = new ParadaModel(aux.getDataHora().plusDays(diffDias), aux.getPlataforma(), aux.getTipo(), aux.getLugar(), viajeNew, viajeNew.getEmpresa());
            paradaModelSave.add(auxParada);
        }

        PrecioModel precioAux;
        for (PrecioModel precio : viaje.getPrecios()) {
            precioAux = new PrecioModel(precio.getPrecio(), precio.getNPiso(), viaje.getAutobus().getPisoByNumero(precio.getNPiso()).getNSillas(), viajeNew, viajeNew.getEmpresa());
            preciosModelSave.add(precioAux);
        }

        viajeRepository.save(viajeNew);
        paradaRepository.saveAll(paradaModelSave);
        precioRepository.saveAll(preciosModelSave);
        return 1;
    }


    public int saveCopyDay(ViajeDTOFormCopy dto, AutobusModel autobus) {
        LocalDateTime dataViajeOriginal;
        LocalDateTime startDaySerch = dto.dataNovo().atTime(0, 0, 0, 0);
        LocalDateTime endDaySearch = startDaySerch.plusDays(1).minusSeconds(1);

        Sort sort = Sort.by("dataHoraSalida").ascending();
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);
        Page<ViajeDTOJPQL> viajes = viajeRepository.findByEmpresaIdAndAutobusId(autobus.getEmpresa().getId(), autobus.getId(), startDaySerch, endDaySearch, pageable);

        if (viajes.getContent().isEmpty())
            return 1;

        long diffDias;

        dataViajeOriginal = viajes.getContent().get(0).salida().getDataHora();
        LocalDateTime firsHourViaje = viajes.getContent().get(0).salida().getDataHora();
        LocalDateTime lastHourViaje = viajes.getContent().get(viajes.getContent().size() - 1).salida().getDataHora();

        diffDias = ChronoUnit.DAYS.between(firsHourViaje.toLocalDate(), lastHourViaje.toLocalDate());
        diffDias = (diffDias < 0) ? -diffDias : diffDias;

        firsHourViaje = dto.dataNovo().atTime(firsHourViaje.getHour(), firsHourViaje.getMinute(), firsHourViaje.getSecond(), firsHourViaje.getNano());
        lastHourViaje = dto.dataNovo().atTime(lastHourViaje.getHour(), lastHourViaje.getMinute(), lastHourViaje.getSecond(), lastHourViaje.getNano());


        if (diffDias > 0)
            lastHourViaje = lastHourViaje.plusDays(diffDias);

        boolean existe = tiempoViajeService.existsViajesActiveFromAutobus(
                autobus.getEmpresa().getId(), autobus.getId(), firsHourViaje, lastHourViaje);
        if (existe)
            return -1;

        List<ViajeModel> viajeModelsSave = new LinkedList<>();
        List<ParadaModel> paradaModelSave = new LinkedList<>();

        ViajeModel viajeAuxiliar;

        diffDias = ChronoUnit.DAYS.between(dataViajeOriginal.toLocalDate(), firsHourViaje.toLocalDate());
        diffDias = (diffDias < 0) ? -diffDias : diffDias;


        for (ViajeDTOJPQL viajeDTOJPQL : viajes.getContent()) {
            viajeAuxiliar = viajeDTOJPQL.viaje();
            viajeAuxiliar.setCodigo(null);
            viajeAuxiliar.setDataHoraSalida(viajeAuxiliar.getDataHoraSalida().plusDays(diffDias));
            for (ParadaModel aux : viajeAuxiliar.getParadas()) {
                aux.setId(null);
                aux.setViaje(viajeAuxiliar);
                aux.setDataHora(aux.getDataHora().plusDays(diffDias));
                paradaModelSave.add(aux);
            }
            viajeModelsSave.add(viajeAuxiliar);
        }
        viajeRepository.saveAll(viajeModelsSave);
        paradaRepository.saveAll(paradaModelSave);
        return 1;
    }

    public ViajeDTOUpdate update(ViajeModel model, AutobusModel autobus) {//Validacao para que a mudanca seja feita
        boolean viajeInIntervalo = tiempoViajeService.existsViajesActiveFromAutobus(
                autobus.getEmpresa().getId(),
                autobus.getId(),
                model.getSalida().getDataHora(),
                model.getDestino().getDataHora());
        if (viajeInIntervalo)
            throw new ValidationException("idAutobus", "El autobus esta ocupado con otro viaje");

        int size = model.getAutobus().getPisos().size();
        if (size != autobus.getPisos().size())
            throw new ValidationException(new FieldMessage("idAutobus", "El autobus no es compatible"));

        PisoModel pisoModel, pisoAutobus;
        for (int i = 1; i <= model.getAutobus().getPisos().size(); i++) {
            pisoModel = model.getAutobus().getPisoByNumero(i);
            pisoAutobus = autobus.getPisoByNumero(i);
            if (!pisoModel.getNSillas().equals(pisoAutobus.getNSillas()))
                throw new ValidationException(new FieldMessage("idAutobus", "El autobus no es compatible"));
        }

        model.setAutobus(autobus);
        var update = viajeRepository.save(model);
        return new ViajeDTOUpdate(update.getCodigo(), autobus.getId());
    }


    @Transactional
    public void delete(ViajeModel model) {
        precioRepository.deleteAll(model.getPrecios());
        paradaRepository.deleteAll(model.getParadas());
        viajeRepository.delete(model);
    }

    public boolean hasPasajes(List<PrecioModel> precios) {
        Integer nPasajes;
        for (PrecioModel precio : precios) {
            nPasajes = precioRepository.calculateNPasajes(precio.getId());
            if (nPasajes != null && nPasajes > 0)
                return true;
        }
        return false;
    }

}