package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.services.AutobusService;
import com.alvaro.empresas.passagens.configurations.exceptions.InternalException.GeneralException;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.precios.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacaoEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacaoFromAutobus;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacaoFromEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOCreate;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOFormCopy;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOListBusquedaEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.JPQL.ViajeDTOJPQL;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOUpdate;
import com.alvaro.empresas.passagens.enums.TypeParada;
import com.alvaro.empresas.passagens.helpers.DateAuxiliarFunctions;
import com.alvaro.empresas.passagens.helpers.thymeleaf.PDFThymeleaf;
import com.alvaro.empresas.passagens.helpers.thymeleaf.PasajeItemListTHModel;
import com.alvaro.empresas.passagens.helpers.validators.AutobusEnabled;
import com.alvaro.empresas.passagens.helpers.validators.EmpresaEnabled;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.alvaro.empresas.passagens.helpers.DateAuxiliarFunctions.copyLocalTimeInLocalDate;


@Service
public class ViajeEmpresaService {
    @Autowired
    private TiempoViajeService tiempoViajeService;
    @Autowired
    private EmpresaService emrEmpresaService;
    @Autowired
    private ViajeRepository viajeRepository;
    @Autowired
    private ParadaRepository paradaRepository;
    @Autowired
    private PrecioService precioService;
    @Autowired
    private LugarRepository lugarRepository;
    @Autowired
    private DateAuxiliarFunctions helperDate;
    @Autowired
    private PrecioRepository precioRepository;
    @Autowired
    private PDFThymeleaf pdfThymeleaf;
    @Autowired
    private EmpresaEnabled empresaEnabled;
    @Autowired
    private AutobusEnabled autobusEnabled;
    @Autowired
    private AutobusService autobusService;

    public ViajeModel findById(UUID id) {
        var model = viajeRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, ViajeModel.class.getName()));
    }

    public Page<ViajeDTOListBusquedaEmpresa> findAllFromEmpresaBetweenDates(ViajeDTOSolicitacaoFromEmpresa solicitacao, Pageable pageable) {
        var empresa = emrEmpresaService.findById(solicitacao.idEmpresa());
        Page<ViajeDTOJPQL> models;
        LocalDateTime dataInicio = helperDate.getFirstDayOfMonthDate(solicitacao.dataAnalise());
        LocalDateTime dataFim = helperDate.getLastDayOfMonthDate(solicitacao.dataAnalise());

        models = viajeRepository.findByEmpresaIdAndStartInInterval(empresa.getId(), dataInicio, dataFim, pageable);

        return models.map(model -> {
            if (model.salida() == null || model.destino() == null)
                throw new GeneralException("Hay un viaje que no posse ninguna parada");
            return new ViajeDTOListBusquedaEmpresa(model.viaje());
        });
    }

    public Page<ViajeDTOListBusquedaEmpresa> findAllFromAutobus(AutobusModel autobusModel, ViajeDTOSolicitacaoFromAutobus solicitacao, Pageable pageable) {
        LocalDateTime dataInicio = helperDate.getFirstDayOfMonthDate(solicitacao.dataAnalise());
        LocalDateTime dataFim = helperDate.getLastDayOfMonthDate(solicitacao.dataAnalise());

        Page<ViajeDTOJPQL> models = viajeRepository.findByEmpresaIdAndAutobusId(autobusModel.getEmpresa().getId(), autobusModel.getId(), dataInicio, dataFim, pageable);

        return models.map(model -> {
            if (model.salida() == null || model.destino() == null)
                throw new ValidationException("lista", "Hay un viaje que no posse ninguna parada");
            return new ViajeDTOListBusquedaEmpresa(model.viaje());
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
                List<ViajeEmpresaDTOJPQ> salidasDia = paradaRepository.loadViajesDayByEmpresaId(idEmpresa, lugarSalida.getId(), lugarDestino.getId(), startDay, endDay);
                for (ViajeEmpresaDTOJPQ ViajeEmpresaDTOJPQ : salidasDia) {
                    salidaDTO = new ParadaDTOComplete(ViajeEmpresaDTOJPQ.getSalida());
                    destinoDTO = new ParadaDTOComplete(ViajeEmpresaDTOJPQ.getDestino());
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
            List<ViajeEmpresaDTOJPQ> salidasDia = paradaRepository.loadViajesDayByEmpresaOnlySalida(idEmpresa, lugarSalida.getId(), startDay, endDay);
            for (ViajeEmpresaDTOJPQ ViajeEmpresaDTOJPQ : salidasDia) {
                salidaDTO = new ParadaDTOComplete(ViajeEmpresaDTOJPQ.getSalida());
                destinoDTO = new ParadaDTOComplete(ViajeEmpresaDTOJPQ.getDestino());

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
    public ViajeDTOEmpresaResponse save(ViajeDTOCreate dto, AutobusModel autobus) {
        autobusEnabled.validAutobusEnabled(dto.idAutobus());
        empresaEnabled.validEmpresaEnabled(autobus.getEmpresaId());

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

        boolean viajeInIntervalo = tiempoViajeService.existsViajesActiveFromAutobus(autobus, dataHoraSalidaAjustada, dataHoraDestinoAjustada);
        if (viajeInIntervalo)
            throw new ValidationException("destino.dataHora", "Existe un viaje del autobus que ocurre en este intervalo");

        var model = new ViajeModel(autobus, autobus.getEmpresa(), dataHoraSalidaAjustada);
        viajeRepository.save(model);

        var salida = new ParadaModel(dataHoraSalidaAjustada, dto.salida().plataforma(), TypeParada.SALIDA, lugarSalida.get(), model, model.getEmpresa());
        var destino = new ParadaModel(dataHoraDestinoAjustada, dto.destino().plataforma(), TypeParada.DESTINO, lugarDestino.get(), model, model.getEmpresa());

        //Tratando los precios del viaje
        List<PrecioModel> precios = new ArrayList<>();
        List<BigDecimal> preciodDto = List.of(dto.precioPiso1(), dto.precioPiso2());

        PisoModel aux;
        for (int i = 1; i <= autobus.getPisos().size(); i++) {
            aux = autobus.getPisoByNumero(i);
            BigDecimal precioItemList = preciodDto.get(i - 1);
            if (precioItemList == null) precioItemList = preciodDto.get(i - 2);
            precios.add(new PrecioModel(precioItemList, i, aux.getNSillas()));
        }

        //Guardando los precios
        List<PrecioDTO> preciosSalvos = precioService.saveAll(precios, model);

        List<ParadaDTOComplete> paradas = new ArrayList<>();
        paradaRepository.save(salida);
        paradas.add(new ParadaDTOComplete(salida));
        paradaRepository.save(destino);
        paradas.add(new ParadaDTOComplete(destino));

        return new ViajeDTOEmpresaResponse(model, paradas, preciosSalvos);
    }

    @Transactional
    public void saveOneCopy(ViajeDTOFormCopy dto, ViajeModel viaje) {
        autobusEnabled.validAutobusEnabled(viaje.getAutobusId());
        empresaEnabled.validEmpresaEnabled(viaje.getEmpresaId());
        long diffDias;
        LocalDateTime dataViajeOriginal = viaje.getDataHoraSalida();

        if (dto.dataNovo().isEqual(viaje.getDataHoraSalida().toLocalDate()))
            throw new ValidationException("dataNovo", "La nueva fecha es identico al del viaje");

        LocalDateTime firsHourViaje = viaje.getSalida().getDataHora();
        LocalDateTime lastHourViaje = viaje.getDestino().getDataHora();

        diffDias = ChronoUnit.DAYS.between(firsHourViaje.toLocalDate(), lastHourViaje.toLocalDate());

        firsHourViaje = copyLocalTimeInLocalDate(dto.dataNovo(), firsHourViaje);
        lastHourViaje = copyLocalTimeInLocalDate(dto.dataNovo(), lastHourViaje);

        diffDias = (diffDias < 0) ? -diffDias : diffDias;
        if (diffDias > 0) lastHourViaje = lastHourViaje.plusDays(diffDias);
        boolean existe = tiempoViajeService.existsViajesActiveFromAutobus(viaje.getAutobus(), firsHourViaje, lastHourViaje);


        if (existe)
            throw new GeneralException(HttpStatus.CONFLICT, "El autobus esta ocupado en esa fecha con otro viaje");

        diffDias = ChronoUnit.DAYS.between(dataViajeOriginal.toLocalDate(), firsHourViaje.toLocalDate());
        diffDias = (diffDias < 0) ? -diffDias : diffDias;

        List<ParadaModel> paradaModelSave = new ArrayList<>();
        List<PrecioModel> preciosModelSave = new ArrayList<>();
        var viajeNew = new ViajeModel(viaje.getAutobus(), viaje.getEmpresa(), viaje.getDataHoraSalida().plusDays(diffDias));

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
    }

    public ViajeDTOUpdate update(ViajeDTOUpdate dto) {//Validacao para que a mudanca seja feita
        var model = findById(dto.codigo());
        empresaEnabled.validEmpresaEnabled(model.getEmpresaId());

        var autobus = autobusService.findById(dto.idAutobus());
        if (!autobus.getEmpresaId().equals(model.getEmpresaId()))
            throw new GeneralException(HttpStatus.CONFLICT, "Este autobus le pertenece a otra empresa");
        autobusEnabled.validAutobusEnabled(dto.idAutobus());

        boolean viajeInIntervalo = tiempoViajeService.existsViajesActiveFromAutobus(autobus, model.getSalida().getDataHora(), model.getDestino().getDataHora());
        if (viajeInIntervalo) throw new ValidationException("idAutobus", "El autobus esta ocupado con otro viaje");

        int size = model.getAutobus().getPisos().size();
        if (size != autobus.getPisos().size())
            throw new ValidationException("idAutobus", "El autobus no es compatible");

        PisoModel pisoModel, pisoAutobus;
        for (int i = 1; i <= model.getAutobus().getPisos().size(); i++) {
            pisoModel = model.getAutobus().getPisoByNumero(i);
            pisoAutobus = autobus.getPisoByNumero(i);
            if (!pisoModel.getNSillas().equals(pisoAutobus.getNSillas()))
                throw new ValidationException("idAutobus", "El autobus no es compatible");
        }

        model.setAutobus(autobus);
        viajeRepository.save(model);
        return new ViajeDTOUpdate(model);
    }


    @Transactional
    public void delete(ViajeModel model) {
        empresaEnabled.validEmpresaEnabled(model.getEmpresaId());
        if (this.hasPasajes(model.getPrecios()))
            throw new GeneralException("El viaje ya posse un pasaje registrado");
        precioRepository.deleteAll(model.getPrecios());
        paradaRepository.deleteAll(model.getParadas());
        viajeRepository.delete(model);
    }

    public boolean hasPasajes(List<PrecioModel> precios) {
        Integer nPasajes;
        for (PrecioModel precio : precios) {
            nPasajes = precioRepository.calculateNPasajes(precio.getId());
            if (nPasajes != null && nPasajes > 0) return true;
        }
        return false;
    }

}