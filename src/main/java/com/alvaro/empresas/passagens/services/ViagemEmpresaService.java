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
import com.alvaro.empresas.passagens.helpers.validators.ValidEnabledEntities;
import com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViagemEmpresaDTOJPQ;
import com.alvaro.empresas.passagens.repositories.ViagemRepository;
import com.alvaro.empresas.passagens.services.validacao.TempoViagemService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import com.alvaro.empresas.passagens.onibus.models.OnibusModel;
import com.alvaro.empresas.passagens.onibus.models.PisoModel;
import com.alvaro.empresas.passagens.onibus.services.OnibusService;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.dtos.precos.PrecoDTO;
import com.alvaro.empresas.passagens.dtos.viagens.ViagemDTOUpdate;
import com.alvaro.empresas.passagens.dtos.viagens.busca.ViagemDTOSolicitacaoEmpresa;
import com.alvaro.empresas.passagens.dtos.viagens.busca.ViagemDTOSolicitacaoFromOnibus;
import com.alvaro.empresas.passagens.dtos.viagens.busca.ViagemDTOSolicitacaoFromEmpresa;
import com.alvaro.empresas.passagens.dtos.viagens.empresa.ViagemDTOCreate;
import com.alvaro.empresas.passagens.dtos.viagens.empresa.ViagemDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.viagens.empresa.ViagemDTOFormCopy;
import com.alvaro.empresas.passagens.dtos.viagens.empresa.ViagemDTOListBusquedaEmpresa;
import com.alvaro.empresas.passagens.dtos.viagens.JPQL.ViagemDTOJPQL;
import com.alvaro.empresas.passagens.helpers.DateAuxiliarFunctions;
import com.alvaro.empresas.passagens.helpers.thymeleaf.PDFThymeleaf;
import com.alvaro.empresas.passagens.helpers.thymeleaf.PasajeItemListTHModel;
import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.PrecoRepository;
import com.itextpdf.kernel.geom.PageSize;

@Service
public class ViagemEmpresaService {
    @Autowired
    private TempoViagemService tiempoviagemService;
    @Autowired
    private EmpresaService empresaService;
    @Autowired
    private ViagemRepository viagemRepository;
    @Autowired
    private ParadaRepository paradaRepository;
    @Autowired
    private PrecoService precoService;
    @Autowired
    private LugarRepository lugarRepository;
    @Autowired
    private DateAuxiliarFunctions helperDate;
    @Autowired
    private PrecoRepository precoRepository;
    @Autowired
    private PDFThymeleaf pdfThymeleaf;
    @Autowired
    private OnibusService onibusService;

    public ViagemModel findById(UUID id) {
        var model = viagemRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, ViagemModel.class.getSimpleName()));
    }

    public Page<ViagemDTOListBusquedaEmpresa> findAllByEmpresaBetweenDates(ViagemDTOSolicitacaoFromEmpresa solicitacao,
                                                                           Pageable pageable) {
        var empresa = empresaService.findById(solicitacao.idEmpresa());
        Page<ViagemDTOJPQL> models;
        LocalDateTime dataInicio = helperDate.getFirstDayOfMonth(solicitacao.dataAnalise());
        LocalDateTime dataFim = helperDate.getLastDayOfMonth(solicitacao.dataAnalise());

        models = viagemRepository.findByEmpresaAndStartInInterval(empresa.getId(), dataInicio, dataFim, pageable);

        return models.map(model -> {
            if (model.saida() == null || model.destino() == null)
                throw new RestRuntimeException("Existe uma viagem que não possui nenhuma parada");
            return new ViagemDTOListBusquedaEmpresa(model.viagem());
        });
    }

    public Page<ViagemDTOListBusquedaEmpresa> findAllFromAutobus(OnibusModel onibusModel,
                                                                 ViagemDTOSolicitacaoFromOnibus solicitacao, Pageable pageable) {
        LocalDateTime dataInicio = helperDate.getLastDayOfMonth(solicitacao.dataAnalise());
        LocalDateTime dataFim = helperDate.getLastDayOfMonth(solicitacao.dataAnalise());

        Page<ViagemDTOJPQL> models = viagemRepository.findByEmpresaAndAutobusAndStartInInterval(
                onibusModel.getEmpresa().getId(), onibusModel.getId(), dataInicio, dataFim, pageable);

        return models.map(model -> {
            if (model.saida() == null || model.destino() == null)
                throw new ValidationException("lista", "Existe uma viagem que não possui nenhuma parada");
            return new ViagemDTOListBusquedaEmpresa(model.viagem());
        });
    }

    public List<ViagemDTOListBusquedaEmpresa> getViagensFrom(UUID idEmpresa, ViagemDTOSolicitacaoEmpresa dto) {
        if (dto.idCidadeDestino().equals(dto.idCidadeSaida()))
            throw new ValidationException("idDestino", "El destino no puede ser el mismo que la saida");

        List<LugarModel> lugaressaida = lugarRepository.findByCiudadId(dto.idCidadeSaida());
        List<LugarModel> lugaresDestino = lugarRepository.findByCiudadId(dto.idCidadeDestino());

        if (lugaressaida.isEmpty())
            throw new ObjectNotFoundException(dto.idCidadeSaida(), CidadeModel.class.getName());

        if (lugaresDestino.isEmpty())
            throw new ObjectNotFoundException(dto.idCidadeDestino(), CidadeModel.class.getName());

        LocalDateTime startDay = dto.dataSaida().atTime(LocalTime.MIN);
        LocalDateTime endDay = dto.dataSaida().atTime(LocalTime.MAX);

        List<ViagemDTOListBusquedaEmpresa> viagemsSelecionados = new ArrayList<>();
        ParadaDTOComplete saidaDTO;
        ParadaDTOComplete destinoDTO;
        List<PrecoDTO> precios;

        for (LugarModel lugarsaida : lugaressaida) {
            for (LugarModel lugarDestino : lugaresDestino) {
                List<ViagemEmpresaDTOJPQ> saidasDia = viagemRepository.findByEmpresaAndStartInInterval(idEmpresa,
                        lugarsaida.getId(), lugarDestino.getId(), startDay, endDay);
                for (ViagemEmpresaDTOJPQ viagemEmpresaDTOJPQ : saidasDia) {
                    saidaDTO = new ParadaDTOComplete(viagemEmpresaDTOJPQ.salida());
                    destinoDTO = new ParadaDTOComplete(viagemEmpresaDTOJPQ.destino());
                    if (!destinoDTO.dataHora().isAfter(saidaDTO.dataHora()))
                        continue;

                    precios = new ArrayList<>();
                    for (PrecoModel precio : viagemEmpresaDTOJPQ.viaje().getPrecos())
                        if (!precio.getCheio())
                            precios.add(new PrecoDTO(precio));
                    viagemsSelecionados.add(new ViagemDTOListBusquedaEmpresa(viagemEmpresaDTOJPQ.viaje(), null,
                            saidaDTO, destinoDTO, precios));
                }
            }
        }

        return viagemsSelecionados;
    }

    public List<ViagemDTOListBusquedaEmpresa> getviagemsFromsaida(UUID idEmpresa, ViagemDTOSolicitacaoEmpresa dto) {
        List<LugarModel> lugaressaida = lugarRepository.findByCiudadId(dto.idCidadeSaida());
        if (lugaressaida.isEmpty())
            throw new ObjectNotFoundException(dto.idCidadeSaida(), CidadeModel.class.getName());

        LocalDateTime startDay = dto.dataSaida().atTime(LocalTime.MIN);
        LocalDateTime endDay = dto.dataSaida().atTime(LocalTime.MAX);

        List<ViagemDTOListBusquedaEmpresa> viagemsSelecionados = new ArrayList<>();
        ParadaDTOComplete saidaDTO;
        ParadaDTOComplete destinoDTO;
        List<PrecoDTO> precios;

        for (LugarModel lugarsaida : lugaressaida) {
            List<ViagemEmpresaDTOJPQ> saidasDia = viagemRepository.findByEmpresaAndStartInInterval(idEmpresa,
                    lugarsaida.getId(), startDay, endDay);
            for (ViagemEmpresaDTOJPQ viagemEmpresaDTOJPQ : saidasDia) {
                saidaDTO = new ParadaDTOComplete(viagemEmpresaDTOJPQ.salida());
                destinoDTO = new ParadaDTOComplete(viagemEmpresaDTOJPQ.destino());

                if (!destinoDTO.dataHora().isAfter(saidaDTO.dataHora()))
                    continue;
                precios = new ArrayList<>();
                for (PrecoModel precio : viagemEmpresaDTOJPQ.viaje().getPrecos())
                    precios.add(new PrecoDTO(precio));
                viagemsSelecionados.add(new ViagemDTOListBusquedaEmpresa(viagemEmpresaDTOJPQ.viaje(), null, saidaDTO,
                        destinoDTO, precios));
            }
        }

        return viagemsSelecionados;
    }

    public byte[] getPdfFromviagem(UUID id) {
        var model = this.findById(id);
        List<PassagemModel> pasajes = new ArrayList<>();
        for (PrecoModel precio : model.getPrecos())
            pasajes.addAll(precio.getPassagens());

        List<PasajeItemListTHModel> pasajesTh = pasajes.stream().map(PasajeItemListTHModel::new).toList();
        Context context = new Context();
        context.setVariable("pasajes", pasajesTh);
        PageSize pageSize = new PageSize(PageSize.A4.getHeight(), PageSize.A4.getWidth());
        return pdfThymeleaf.generatePDFByTemplate("/empresa/pasajerosList", context, PageSize.A4);
    }

    @Transactional
    public ViagemDTOEmpresaResponse save(ViagemDTOCreate dto, OnibusModel autobus) {
        var onibus = onibusService.findById(dto.idOnibus());
        var empresa = empresaService.findById(autobus.getEmpresaId());
        ValidEnabledEntities.validEmpresa(empresa);
        ValidEnabledEntities.validOnibus(onibus);
        var lugarsaida = lugarRepository.findById(dto.idLugarSaida());
        if (lugarsaida.isEmpty())
            throw new ValidationException("idLugarSaida", "El origen no fue allado");
        var lugarDestino = lugarRepository.findById(dto.idLugarDestino());
        if (lugarDestino.isEmpty())
            throw new ValidationException("idLugarDestino", "El destino no fue allado");

        if (dto.idLugarDestino().equals(dto.idLugarSaida()))
            throw new ValidationException("idLugarDestino", "El destino es el mismo que la saida");

        LocalDateTime dataHorasaidaAjustada = dto.dataSaida().withSecond(0).withNano(0);
        LocalDateTime dataHoraDestino = dataHorasaidaAjustada.plusHours(dto.tempoViagem());

        if (!tiempoviagemService.validarTempoMaximoViaje(dataHorasaidaAjustada, dataHoraDestino))
            throw new RestRuntimeException(HttpStatus.CONFLICT, "Un viagem puede durar maximo 3 dias");

        boolean viagemInIntervalo = tiempoviagemService.existsViajesActiveFromAutobus(autobus, dataHorasaidaAjustada,
                dataHoraDestino);
        if (viagemInIntervalo)
            throw new RestRuntimeException(HttpStatus.CONFLICT, "El autobus estara ocupado con otro viagem");

        var model = new ViagemModel(autobus, dataHorasaidaAjustada);

        var saida = new ParadaModel(dataHorasaidaAjustada, dto.plataforma(), TipoParada.SAIDA, lugarsaida.get(), model);
        var destino = new ParadaModel(dataHoraDestino, 0, TipoParada.DESTINO, lugarDestino.get(), model);

        // Tratando los precios del viagem
        List<PrecoModel> precios = new ArrayList<>();
        List<BigDecimal> preciodDto = List.of(dto.precoPiso1(), dto.precoPiso2());

        PisoModel aux;
        for (int i = 1; i <= autobus.getPisos().size(); i++) {
            aux = autobus.getPisoByNumero(i);
            BigDecimal precioItemList = preciodDto.get(i - 1);
            if (precioItemList == null)
                precioItemList = preciodDto.get(i - 2);
            precios.add(new PrecoModel(precioItemList, i, aux.getNSillas()));
        }

        viagemRepository.save(model);
        // Guardando los precios
        List<PrecoDTO> preciosSalvos = precoService.saveAll(precios, model);
        model.addParada(saida);
        model.addParada(destino);
        viagemRepository.save(model);

        List<ParadaDTOComplete> paradas = new ArrayList<>();
        paradas.add(new ParadaDTOComplete(saida));
        paradas.add(new ParadaDTOComplete(destino));
        return new ViagemDTOEmpresaResponse(model, paradas, preciosSalvos);
    }

    @Transactional
    // Verificar si se quiere replicar un viagem futuro en un viagem menos futuro
    public ViagemModel saveOneCopy(ViagemDTOFormCopy dto, ViagemModel viagem) {
        ValidEnabledEntities.validEmpresa(viagem.getEmpresa());
        ValidEnabledEntities.validOnibus(viagem.getOnibus());

        if (dto.dataNovo().isEqual(viagem.getDataHoraSaida().toLocalDate()))
            throw new ValidationException("dataNova", "La nueva fecha es identico al del viagem");

        LocalDateTime firsHourviagem = viagem.getSaida().getDataHora();
        LocalDateTime lastHourviagem = viagem.getDestino().getDataHora();
        var durationviagemSeconds = ChronoUnit.SECONDS.between(firsHourviagem, lastHourviagem);

        firsHourviagem = copyLocalTimeInLocalDate(dto.dataNovo(), firsHourviagem);
        lastHourviagem = firsHourviagem.plusSeconds(durationviagemSeconds);

        boolean existe = tiempoviagemService.existsviagemsActiveFromAutobus(viagem.getOnibus(), firsHourviagem,
                lastHourviagem);

        if (existe)
            throw new RestRuntimeException(HttpStatus.CONFLICT, "El autobus esta ocupado en esa fecha con otro viagem");

        viagem.getOnibus().setEmpresa(viagem.getEmpresa());
        var viagemNew = new ViagemModel(viagem.getOnibus(), firsHourviagem);

        var horaPartida = firsHourviagem;
        long diffSecondBetweenParadas;
        var dataHoraInicioviagemOld = viagem.getsaida().getDataHora();
        ParadaModel auxParada;
        for (ParadaModel aux : viagem.getParadas()) {
            diffSecondBetweenParadas = ChronoUnit.SECONDS.between(dataHoraInicioviagemOld, aux.getDataHora());
            auxParada = new ParadaModel(horaPartida.plusSeconds(diffSecondBetweenParadas), aux.getPlataforma(),
                    aux.getTipo(), aux.getLugar(), viagemNew);
            viagemNew.addParada(auxParada);
        }

        PrecoModel precioAux;
        for (PrecoModel precio : viagem.getPrecos()) {
            precioAux = new PrecoModel(precio.getPreco(), precio.getNPiso(),
                    viagem.getOnibus().getPisoByNumero(precio.getNPiso()).getNAssentos(), viagemNew);
            viagemNew.addPreco(precioAux);
        }

        return viagemRepository.save(viagemNew);
    }

    public ViagemDTOUpdate update(ViagemDTOUpdate dto) {// Validacao para que a mudanca seja feita
        var model = findById(dto.idViagem());
        ValidEnabledEntities.validEmpresa(model.getEmpresa());

        var autobus = onibusService.findById(dto.idOnibus());
        if (!autobus.getEmpresaId().equals(model.getEmpresaId()))
            throw new RestRuntimeException(HttpStatus.CONFLICT, "Este autobus le pertenece a otra empresa");
        ValidEnabledEntities.validOnibus(autobus);

        boolean viagemInIntervalo = tiempoviagemService.existsViajesActiveFromAutobus(autobus,
                model.getSaida().getDataHora(), model.getDestino().getDataHora());
        if (viagemInIntervalo)
            throw new ValidationException("idAutobus", "El autobus esta ocupado con otro viagem");

        int size = model.getOnibus().getPisos().size();
        if (size != autobus.getPisos().size())
            throw new ValidationException("idAutobus", "El autobus no es compatible");

        PisoModel pisoModel, pisoAutobus;
        for (int i = 1; i <= model.getOnibus().getPisos().size(); i++) {
            pisoModel = model.getOnibus().getPisoByNumero(i);
            pisoAutobus = autobus.getPisoByNumero(i);
            if (!pisoModel.getNSillas().equals(pisoAutobus.getNSillas()))
                throw new ValidationException("idAutobus", "El autobus no es compatible");
        }

        model.setOnibus(autobus);
        viagemRepository.save(model);
        return new ViagemDTOUpdate(model);
    }

    @Transactional
    public void delete(ViagemModel model) {
        ValidEnabledEntities.validEmpresa(model.getEmpresa());
        if (this.hasPasajes(model.getPrecos()))
            throw new RestRuntimeException("El viagem ya posse un pasaje registrado");
        precoRepository.deleteAll(model.getPrecos());
        paradaRepository.deleteAll(model.getParadas());
        viagemRepository.delete(model);
    }

    public boolean hasPasajes(List<PrecoModel> precios) {
        Integer nPasajes;
        for (PrecoModel precio : precios) {
            nPasajes = precoRepository.calculateNPasajes(precio.getId());
            if (nPasajes != null && nPasajes > 0)
                return true;
        }
        return false;
    }

}