package com.alvaro.empresas.passagens.services;

import static com.alvaro.empresas.passagens.helpers.DateAuxiliarFunctions.copyLocalTimeInLocalDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.enums.TipoParada;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import com.alvaro.empresas.passagens.onibus.models.AutobusModel;
import com.alvaro.empresas.passagens.onibus.models.PisoModel;
import com.alvaro.empresas.passagens.onibus.services.AutobusService;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.dtos.precos.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOUpdate;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacaoEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacaoFromAutobus;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacaoFromEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOCreate;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOFormCopy;
import com.alvaro.empresas.passagens.dtos.viajes.Empresa.ViajeDTOListBusquedaEmpresa;
import com.alvaro.empresas.passagens.dtos.viajes.JPQL.ViajeDTOJPQL;
import com.alvaro.empresas.passagens.helpers.DateAuxiliarFunctions;
import com.alvaro.empresas.passagens.helpers.thymeleaf.PDFThymeleaf;
import com.alvaro.empresas.passagens.helpers.thymeleaf.PasajeItemListTHModel;
import com.alvaro.empresas.passagens.helpers.validators.AutobusEnabled;
import com.alvaro.empresas.passagens.helpers.validators.EmpresaEnabled;
import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViajeEmpresaDTOJPQ;
import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.PrecioRepository;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import com.alvaro.empresas.passagens.services.validacao.TiempoViajeService;
import com.itextpdf.kernel.geom.PageSize;


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

    public ViagemModel findById(UUID id) {
        var model = viajeRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, ViagemModel.class.getName()));
    }

    public Page<ViajeDTOListBusquedaEmpresa> findAllByEmpresaBetweenDates(ViajeDTOSolicitacaoFromEmpresa solicitacao, Pageable pageable) {
        var empresa = emrEmpresaService.findById(solicitacao.idEmpresa());
        Page<ViajeDTOJPQL> models;
        LocalDateTime dataInicio = helperDate.getFirstDayOfMonthDate(solicitacao.dataAnalise());
        LocalDateTime dataFim = helperDate.getLastDayOfMonthDate(solicitacao.dataAnalise());

        models = viajeRepository.findByEmpresaAndStartInInterval(empresa.getId(), dataInicio, dataFim, pageable);

        return models.map(model -> {
            if (model.salida() == null || model.destino() == null)
                throw new RestRuntimeException("Hay un viaje que no posse ninguna parada");
            return new ViajeDTOListBusquedaEmpresa(model.viaje());
        });
    }

    public Page<ViajeDTOListBusquedaEmpresa> findAllFromAutobus(AutobusModel autobusModel, ViajeDTOSolicitacaoFromAutobus solicitacao, Pageable pageable) {
        LocalDateTime dataInicio = helperDate.getFirstDayOfMonthDate(solicitacao.dataAnalise());
        LocalDateTime dataFim = helperDate.getLastDayOfMonthDate(solicitacao.dataAnalise());

        Page<ViajeDTOJPQL> models = viajeRepository.findByEmpresaAndAutobusAndStartInInterval(autobusModel.getEmpresa().getId(), autobusModel.getId(), dataInicio, dataFim, pageable);

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
                List<ViajeEmpresaDTOJPQ> salidasDia = viajeRepository.findByEmpresaAndStartInInterval(idEmpresa, lugarSalida.getId(), lugarDestino.getId(), startDay, endDay);
                for (ViajeEmpresaDTOJPQ ViajeEmpresaDTOJPQ : salidasDia) {
                    salidaDTO = new ParadaDTOComplete(ViajeEmpresaDTOJPQ.getSalida());
                    destinoDTO = new ParadaDTOComplete(ViajeEmpresaDTOJPQ.getDestino());
                    if (!destinoDTO.dataHora().isAfter(salidaDTO.dataHora())) continue;

                    precios = new ArrayList<>();
                    for (PrecoModel precio : ViajeEmpresaDTOJPQ.getViaje().getPrecios())
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
            List<ViajeEmpresaDTOJPQ> salidasDia = viajeRepository.findByEmpresaAndStartInInterval(idEmpresa, lugarSalida.getId(), startDay, endDay);
            for (ViajeEmpresaDTOJPQ ViajeEmpresaDTOJPQ : salidasDia) {
                salidaDTO = new ParadaDTOComplete(ViajeEmpresaDTOJPQ.getSalida());
                destinoDTO = new ParadaDTOComplete(ViajeEmpresaDTOJPQ.getDestino());

                if (!destinoDTO.dataHora().isAfter(salidaDTO.dataHora())) continue;
                precios = new ArrayList<>();
                for (PrecoModel precio : ViajeEmpresaDTOJPQ.getViaje().getPrecios())
                    precios.add(new PrecioDTO(precio));
                viajesSelecionados.add(new ViajeDTOListBusquedaEmpresa(ViajeEmpresaDTOJPQ.getViaje(), null, salidaDTO, destinoDTO, precios));
            }
        }

        return viajesSelecionados;
    }

    public byte[] getPdfFromViaje(UUID id) {
        var model = this.findById(id);
        List<PassagemModel> pasajes = new ArrayList<>();
        for (PrecoModel precio : model.getPrecios())
            pasajes.addAll(precio.getPasajes());

        List<PasajeItemListTHModel> pasajesTh = pasajes.stream().map(PasajeItemListTHModel::new).toList();
        Context context = new Context();
        context.setVariable("pasajes", pasajesTh);
        PageSize pageSize = new PageSize(PageSize.A4.getHeight(), PageSize.A4.getWidth());
        return pdfThymeleaf.generatePDFByTemplate("/empresa/pasajerosList", context, PageSize.A4);
    }

    @Transactional
    public ViajeDTOEmpresaResponse save(ViajeDTOCreate dto, AutobusModel autobus) {
        autobusEnabled.validAutobusEnabled(dto.idAutobus());
        empresaEnabled.validEmpresaEnabled(autobus.getEmpresaId());

        var lugarSalida = lugarRepository.findById(dto.idLugarSalida());
        if (lugarSalida.isEmpty()) throw new ValidationException("idLugarSalida", "El origen no fue allado");

        var lugarDestino = lugarRepository.findById(dto.idLugarDestino());
        if (lugarDestino.isEmpty()) throw new ValidationException("idLugarDestino", "El destino no fue allado");

        if (dto.idLugarDestino().equals(dto.idLugarSalida()))
            throw new ValidationException("idLugarDestino", "El destino es el mismo que la salida");

        LocalDateTime dataHoraSalidaAjustada = dto.fechaSalida().withSecond(0).withNano(0);
        LocalDateTime dataHoraDestino = dataHoraSalidaAjustada.plusHours(dto.horasViaje());

        if (!tiempoViajeService.validarTempoMaximoViaje(dataHoraSalidaAjustada, dataHoraDestino))
            throw new RestRuntimeException(HttpStatus.CONFLICT, "Un viaje puede durar maximo 3 dias");

        boolean viajeInIntervalo = tiempoViajeService.existsViajesActiveFromAutobus(autobus, dataHoraSalidaAjustada, dataHoraDestino);
        if (viajeInIntervalo)
            throw new RestRuntimeException(HttpStatus.CONFLICT, "El autobus estara ocupado con otro viaje");

        var model = new ViagemModel(autobus, dataHoraSalidaAjustada);

        var salida = new ParadaModel(dataHoraSalidaAjustada, dto.plataforma(), TipoParada.SALIDA, lugarSalida.get(), model);
        var destino = new ParadaModel(dataHoraDestino, 0, TipoParada.DESTINO, lugarDestino.get(), model);

        //Tratando los precios del viaje
        List<PrecoModel> precios = new ArrayList<>();
        List<BigDecimal> preciodDto = List.of(dto.precioPiso1(), dto.precioPiso2());

        PisoModel aux;
        for (int i = 1; i <= autobus.getPisos().size(); i++) {
            aux = autobus.getPisoByNumero(i);
            BigDecimal precioItemList = preciodDto.get(i - 1);
            if (precioItemList == null) precioItemList = preciodDto.get(i - 2);
            precios.add(new PrecoModel(precioItemList, i, aux.getNSillas()));
        }

        viajeRepository.save(model);
        //Guardando los precios
        List<PrecioDTO> preciosSalvos = precioService.saveAll(precios, model);
        model.addParada(salida);
        model.addParada(destino);
        viajeRepository.save(model);

        List<ParadaDTOComplete> paradas = new ArrayList<>();
        paradas.add(new ParadaDTOComplete(salida));
        paradas.add(new ParadaDTOComplete(destino));
        return new ViajeDTOEmpresaResponse(model, paradas, preciosSalvos);
    }

    @Transactional
    // Verificar si se quiere replicar un viaje futuro en un viaje menos futuro
    public ViagemModel saveOneCopy(ViajeDTOFormCopy dto, ViagemModel viaje) {
        autobusEnabled.validAutobusEnabled(viaje.getAutobusId());
        empresaEnabled.validEmpresaEnabled(viaje.getEmpresaId());

        if (dto.dataNovo().isEqual(viaje.getDataHoraSalida().toLocalDate()))
            throw new ValidationException("dataNova", "La nueva fecha es identico al del viaje");

        LocalDateTime firsHourViaje = viaje.getSalida().getDataHora();
        LocalDateTime lastHourViaje = viaje.getDestino().getDataHora();
        var durationViajeSeconds = ChronoUnit.SECONDS.between(firsHourViaje, lastHourViaje);

        firsHourViaje = copyLocalTimeInLocalDate(dto.dataNovo(), firsHourViaje);
        lastHourViaje = firsHourViaje.plusSeconds(durationViajeSeconds);

        boolean existe = tiempoViajeService.existsViajesActiveFromAutobus(viaje.getAutobus(), firsHourViaje, lastHourViaje);

        if (existe)
            throw new RestRuntimeException(HttpStatus.CONFLICT, "El autobus esta ocupado en esa fecha con otro viaje");

        viaje.getAutobus().setEmpresa(viaje.getEmpresa());
        var viajeNew = new ViagemModel(viaje.getAutobus(), firsHourViaje);

        var horaPartida = firsHourViaje;
        long diffSecondBetweenParadas;
        var dataHoraInicioViajeOld = viaje.getSalida().getDataHora();
        ParadaModel auxParada;
        for (ParadaModel aux : viaje.getParadas()) {
            diffSecondBetweenParadas = ChronoUnit.SECONDS.between(dataHoraInicioViajeOld, aux.getDataHora());
            auxParada = new ParadaModel(horaPartida.plusSeconds(diffSecondBetweenParadas), aux.getPlataforma(), aux.getTipo(), aux.getLugar(), viajeNew);
            viajeNew.addParada(auxParada);
        }

        PrecoModel precioAux;
        for (PrecoModel precio : viaje.getPrecios()) {
            precioAux = new PrecoModel(precio.getPrecio(), precio.getNPiso(), viaje.getAutobus().getPisoByNumero(precio.getNPiso()).getNSillas(), viajeNew);
            viajeNew.addPrecio(precioAux);
        }

        return viajeRepository.save(viajeNew);
    }

    public ViajeDTOUpdate update(ViajeDTOUpdate dto) {//Validacao para que a mudanca seja feita
        var model = findById(dto.codigo());
        empresaEnabled.validEmpresaEnabled(model.getEmpresaId());

        var autobus = autobusService.findById(dto.idAutobus());
        if (!autobus.getEmpresaId().equals(model.getEmpresaId()))
            throw new RestRuntimeException(HttpStatus.CONFLICT, "Este autobus le pertenece a otra empresa");
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
    public void delete(ViagemModel model) {
        empresaEnabled.validEmpresaEnabled(model.getEmpresaId());
        if (this.hasPasajes(model.getPrecios()))
            throw new RestRuntimeException("El viaje ya posse un pasaje registrado");
        precioRepository.deleteAll(model.getPrecios());
        paradaRepository.deleteAll(model.getParadas());
        viajeRepository.delete(model);
    }

    public boolean hasPasajes(List<PrecoModel> precios) {
        Integer nPasajes;
        for (PrecoModel precio : precios) {
            nPasajes = precioRepository.calculateNPasajes(precio.getId());
            if (nPasajes != null && nPasajes > 0) return true;
        }
        return false;
    }

}