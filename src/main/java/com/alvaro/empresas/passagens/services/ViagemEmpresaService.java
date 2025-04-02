package com.alvaro.empresas.passagens.services;

import static com.alvaro.empresas.passagens.helpers.DateAuxiliarFunctions.copyLocalTimeInLocalDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.EntityNotFoundException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.PageOutput;
import com.alvaro.empresas.passagens.dtos.precos.PrecoDTO;
import com.alvaro.empresas.passagens.dtos.viagens.ViagemDTOUpdate;
import com.alvaro.empresas.passagens.dtos.viagens.busca.ViagemDTOSolicitacaoEmpresa;
import com.alvaro.empresas.passagens.dtos.viagens.empresa.ViagemDTOCreate;
import com.alvaro.empresas.passagens.dtos.viagens.empresa.ViagemDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.viagens.empresa.ViagemDTOFormCopy;
import com.alvaro.empresas.passagens.dtos.viagens.empresa.ViagemDTOListBuscaEmpresa;
import com.alvaro.empresas.passagens.enums.TipoParada;
import com.alvaro.empresas.passagens.helpers.DateAuxiliarFunctions;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.helpers.thymeleaf.PDFThymeleaf;
import com.alvaro.empresas.passagens.helpers.thymeleaf.PassagemItemListTHModel;
import com.alvaro.empresas.passagens.helpers.validators.ValidEnabledEntities;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.onibus.models.PisoModel;
import com.alvaro.empresas.passagens.onibus.repositories.OnibusRepository;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaResponseDTO;
import com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViagemEmpresaDTOJPQ;
import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.EmpresaRepository;
import com.alvaro.empresas.passagens.repositories.PrecoRepository;
import com.alvaro.empresas.passagens.repositories.ViagemRepository;
import com.alvaro.empresas.passagens.services.validacao.TempoViagemService;
import com.itextpdf.kernel.geom.PageSize;

@Service
public class ViagemEmpresaService {
    @Autowired
    private TempoViagemService tempoViagemService;
    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private ViagemRepository viagemRepository;
    @Autowired
    private ParadaRepository paradaRepository;
    @Autowired
    private PrecoRepository precoRepository;
    @Autowired
    private LugarRepository lugarRepository;
    @Autowired
    private DateAuxiliarFunctions helperDate;
    @Autowired
    private PDFThymeleaf pdfThymeleaf;
    @Autowired
    private OnibusRepository onibusRepository;
    @Autowired
    private UserLoguedComponent userLogued;

    public ViagemModel findById(UUID id) {
        return viagemRepository.findByIdOrThr(id);
    }

    public PageOutput<ViagemDTOListBuscaEmpresa> findAllByEmpresaBetweenDates(UUID empresaId, String mesAnalise,
            Pageable pageable) {

        Integer[] anoMes = DateAuxiliarFunctions.splitAnoMonth(mesAnalise);

        userLogued.validIfIsAdminOrOwnerEmpresa(empresaId);
        var empresa = empresaRepository.findByIdOrThr(empresaId);

        LocalDateTime dataInicio = helperDate.getFirstDayOfMonth(anoMes);
        LocalDateTime dataFim = helperDate.getLastDayOfMonth(anoMes);

        var page = viagemRepository.findByEmpresaAndStartInInterval(empresa.getId(), dataInicio, dataFim, pageable);

        var dtos = page.map(model -> {
            if (model.saida() == null || model.destino() == null)
                throw new RestRuntimeException(HttpStatus.CONFLICT, "Existe uma viagem sem data de início ou fim");
            return new ViagemDTOListBuscaEmpresa(model.viagem());
        });

        return new PageOutput<>(dtos);
    }

    public PageOutput<ViagemDTOListBuscaEmpresa> findAllFromOnibus(UUID onibusId, String mesAnalise,
            Pageable pageable) {
        var onibus = onibusRepository.findByIdOrThr(onibusId);
        userLogued.validIfIsAdminOrOwnerEmpresa(onibus.getEmpresaId());

        Integer[] anoMes = DateAuxiliarFunctions.splitAnoMonth(mesAnalise);
        LocalDateTime dataInicio = helperDate.getFirstDayOfMonth(anoMes);
        LocalDateTime dataFim = helperDate.getLastDayOfMonth(anoMes);

        var models = viagemRepository.findByEmpresaAndOnibusAndStartInInterval(onibus.getEmpresa().getId(),
                onibus.getId(), dataInicio, dataFim, pageable);

        var dtos = models.map(model -> {
            if (model.saida() == null || model.destino() == null)
                throw new RestRuntimeException(HttpStatus.CONFLICT, "Existe uma viagem sem data de início ou fim");
            return new ViagemDTOListBuscaEmpresa(model.viagem());
        });
        return new PageOutput<>(dtos);
    }

    public List<ViagemDTOListBuscaEmpresa> findViagensByDay(UUID idEmpresa, ViagemDTOSolicitacaoEmpresa dto) {
        userLogued.validIfIsAdminOrOwnerEmpresa(idEmpresa);
        List<LugarModel> lugaresSaida = lugarRepository.findByCidadeId(dto.idCidadeSaida());
        if (lugaresSaida.isEmpty())
            throw new EntityNotFoundException(dto.idCidadeSaida(), CidadeModel.class);

        List<LugarModel> lugaresDestino = null;
        if (dto.idCidadeDestino() != null && dto.idCidadeDestino() != 0) {
            if (dto.idCidadeDestino().equals(dto.idCidadeSaida()))
                throw new ValidationException("idDestino", "O destino não pode ser o mesmo que a saída");
            lugaresDestino = lugarRepository.findByCidadeId(dto.idCidadeDestino());
            if (lugaresDestino.isEmpty())
                throw new EntityNotFoundException(dto.idCidadeDestino(), CidadeModel.class);
        }

        LocalDateTime startDay = dto.dataSaida().atTime(LocalTime.MIN);
        LocalDateTime endDay = dto.dataSaida().atTime(LocalTime.MAX);

        List<ViagemEmpresaDTOJPQ> viagensDisponiveis = new ArrayList<>();

        for (LugarModel saida : lugaresSaida) {
            if (lugaresDestino != null)
                lugaresDestino.forEach(destino -> {
                    var viagensResult = viagemRepository.findByEmpresaAndStartInInterval(idEmpresa, saida.getId(),
                            destino.getId(), startDay, endDay);
                    viagensDisponiveis.addAll(viagensResult);
                });
            else {
                List<ViagemEmpresaDTOJPQ> viagensResult = viagemRepository.findByEmpresaAndStartInInterval(idEmpresa,
                        saida.getId(), startDay, endDay);
                viagensDisponiveis.addAll(viagensResult);
            }
        }

        return viagensDisponiveis.stream().map(viagem -> {
            var saida = new ParadaResponseDTO(viagem.saida());
            var destino = new ParadaResponseDTO(viagem.destino());
            var precos = viagem.viagem().getPrecos();
            var precosDTO = precos.stream().filter(p -> !p.getCheio()).map(PrecoDTO::new).toList();
            return new ViagemDTOListBuscaEmpresa(viagem.viagem(), null, saida, destino, precosDTO);
        }).toList();
    }

    public byte[] getPdfFromViagem(UUID id) {
        var model = this.findById(id);
        List<PassagemItemListTHModel> passagensTh = model.getPrecos().stream().flatMap(p -> p.getPassagens().stream())
                .map(PassagemItemListTHModel::new).toList();

        Context context = new Context();
        context.setVariable("passagens", passagensTh);
        return pdfThymeleaf.generatePDFByTemplate("/empresa/passageirosList", context, PageSize.A4);
    }

    @Transactional
    public ViagemDTOEmpresaResponse save(ViagemDTOCreate dto) {

        var onibus = onibusRepository.findByIdOrThr(dto.idOnibus());
        userLogued.validIfIsMyEmpresa(onibus.getEmpresaId());

        ValidEnabledEntities.validOnibus(onibus);
        ValidEnabledEntities.validEmpresa(onibus.getEmpresa());

        var lugarSaida = lugarRepository.findById(dto.idLugarSaida());
        if (lugarSaida.isEmpty())
            throw new ValidationException("idLugarSaida", "A saida não foi encontrada");

        var lugarDestino = lugarRepository.findById(dto.idLugarDestino());
        if (lugarDestino.isEmpty())
            throw new ValidationException("idLugarDestino", "O destino não foi encontrado");

        if (dto.idLugarDestino().equals(dto.idLugarSaida()))
            throw new ValidationException("idLugarDestino", "O destino não pode ser igual a saida");

        LocalDateTime dataHoraSaidaAjustada = dto.dataSaida().withSecond(0).withNano(0);
        LocalDateTime dataHoraDestino = dataHoraSaidaAjustada.plusHours(dto.tempoViagem());

        try {
            tempoViagemService.validarTempoMaximoViagem(dataHoraSaidaAjustada, dataHoraDestino);
        } catch (Exception e) {
            throw new ValidationException("dataSaida", e.getMessage());
        }

        boolean viajeInIntervalo = tempoViagemService.existsViagensActiveFromOnibus(onibus, dataHoraSaidaAjustada,
                dataHoraDestino);
        if (viajeInIntervalo)
            throw new RestRuntimeException(HttpStatus.CONFLICT,
                    "O ônibus já tem um viaje em andamento nesse intervalo");

        var model = new ViagemModel(onibus, dataHoraSaidaAjustada);

        var saida = new ParadaModel(dataHoraSaidaAjustada, dto.plataforma(), TipoParada.SAIDA, lugarSaida.get(), model);
        var destino = new ParadaModel(dataHoraDestino, 0, TipoParada.DESTINO, lugarDestino.get(), model);

        List<BigDecimal> precosDTO = List.of(dto.precoPiso1(), dto.precoPiso2());

        PisoModel aux;
        for (int i = 1; i <= onibus.getPisos().size(); i++) {
            aux = onibus.getPisoByNumero(i);
            BigDecimal precoItemList = precosDTO.get(i - 1);
            if (precoItemList == null)
                precoItemList = precosDTO.get(i - 2);
            var preco = new PrecoModel(precoItemList, i, aux.getNAssentos());
            preco.setViagem(model);
            preco.setEmpresa(preco.getEmpresa());
            model.addPreco(preco);
        }

        viagemRepository.save(model);

        List<PrecoDTO> precosSalvos = model.getPrecos().stream().map(preco -> new PrecoDTO(preco)).toList();

        model.addParada(saida);
        model.addParada(destino);
        viagemRepository.save(model);

        List<ParadaResponseDTO> paradas = new ArrayList<>();
        paradas.add(new ParadaResponseDTO(saida));
        paradas.add(new ParadaResponseDTO(destino));
        return new ViagemDTOEmpresaResponse(model, paradas, precosSalvos);
    }

    @Transactional
    // Verificar se se deseja replicar uma viagem futura em uma viagem menos futura
    public ViagemModel saveOneCopy(ViagemDTOFormCopy dto) {

        var viagem = viagemRepository.findByIdOrThr(dto.idViagem());
        userLogued.validIfIsMyEmpresa(viagem.getEmpresaId());

        ValidEnabledEntities.validOnibus(viagem.getOnibus());
        ValidEnabledEntities.validEmpresa(viagem.getEmpresa());

        if (dto.dataNovo().isEqual(viagem.getDataHoraSaida().toLocalDate()))
            throw new ValidationException("dataNovo", "A nova data é no mesmo dia que a viagem");

        LocalDateTime primeiraHoraViagem = viagem.getSaida().getDataHora();
        LocalDateTime ultimaHoraViagem = viagem.getDestino().getDataHora();
        var tempoViagemTotal = ChronoUnit.SECONDS.between(primeiraHoraViagem, ultimaHoraViagem);

        primeiraHoraViagem = copyLocalTimeInLocalDate(dto.dataNovo(), primeiraHoraViagem);
        ultimaHoraViagem = primeiraHoraViagem.plusSeconds(tempoViagemTotal);

        boolean existe = tempoViagemService.existsViagensActiveFromOnibus(viagem.getOnibus(), primeiraHoraViagem,
                ultimaHoraViagem);

        if (existe)
            throw new RestRuntimeException(HttpStatus.CONFLICT, "O ônibus está ocupado nessa data com outra viagem");

        viagem.getOnibus().setEmpresa(viagem.getEmpresa());
        var novaViagem = new ViagemModel(viagem.getOnibus(), primeiraHoraViagem);

        var horaPartida = primeiraHoraViagem;
        var dataHoraInicioViagemAntiga = viagem.getSaida().getDataHora();
        viagem.getParadas().forEach((parada) -> {
            var tempoViagemToParada = ChronoUnit.SECONDS.between(dataHoraInicioViagemAntiga, parada.getDataHora());
            var novaParada = new ParadaModel(horaPartida.plusSeconds(tempoViagemToParada), parada.getPlataforma(),
                    parada.getTipo(), parada.getLugar(), novaViagem);
            novaViagem.addParada(novaParada);
        });

        viagem.getPrecos().forEach((p) -> {
            var precoAux = new PrecoModel(p.getPreco(), p.getNPiso(),
                    viagem.getOnibus().getPisoByNumero(p.getNPiso()).getNAssentos(), novaViagem);
            novaViagem.addPreco(precoAux);
        });

        return viagemRepository.save(novaViagem);
    }

    public ViagemDTOUpdate update(UUID id, ViagemDTOUpdate dto) { // Validação para que a mudança seja feita

        var viagemModel = viagemRepository.findByIdOrThr(id);
        userLogued.validIfIsMyEmpresa(viagemModel.getEmpresaId());

        var model = findById(dto.idViagem());
        ValidEnabledEntities.validEmpresa(model.getEmpresa());
        ValidEnabledEntities.validOnibus(model.getOnibus());

        var onibus = onibusRepository.findByIdOrThr(dto.idOnibus());
        if (!onibus.getEmpresaId().equals(model.getEmpresaId()))
            throw new RestRuntimeException(HttpStatus.CONFLICT, "Este ônibus pertence a outra empresa");
        ValidEnabledEntities.validOnibus(onibus);

        boolean isOnibusOcupado = tempoViagemService.existsViagensActiveFromOnibus(onibus,
                model.getSaida().getDataHora(), model.getDestino().getDataHora());
        if (isOnibusOcupado)
            throw new ValidationException("idOnibus", "O ônibus está ocupado com outra viagem");

        int quantidadePisos = model.getOnibus().getPisos().size();
        if (quantidadePisos != onibus.getPisos().size())
            throw new ValidationException("idOnibus", "O ônibus não possui o mesmo número de pisos");

        PisoModel pisoModel, pisoOnibus;
        for (int i = 1; i <= model.getOnibus().getPisos().size(); i++) {
            pisoModel = model.getOnibus().getPisoByNumero(i);
            pisoOnibus = onibus.getPisoByNumero(i);
            if (!pisoModel.getNAssentos().equals(pisoOnibus.getNAssentos()))
                throw new ValidationException("idOnibus", "O ônibus não é compatível");
        }

        model.setOnibus(onibus);
        viagemRepository.save(model);
        return new ViagemDTOUpdate(model);
    }

    @Transactional
    public void delete(UUID id) {
        var model = viagemRepository.findByIdOrThr(id);
        userLogued.validIfIsMyEmpresa(model.getEmpresaId());
        ValidEnabledEntities.validEmpresa(model.getEmpresa());
        if (this.hasPassagens(model.getPrecos()))
            throw new RestRuntimeException("El viaje ya posse un pasaje registrado");
        precoRepository.deleteAll(model.getPrecos());
        paradaRepository.deleteAll(model.getParadas());
        viagemRepository.delete(model);
    }

    public boolean hasPassagens(List<PrecoModel> precos) {
        Integer nPassagens;
        for (PrecoModel preco : precos) {
            nPassagens = precoRepository.calcularNPassagens(preco.getId());
            if (nPassagens != null && nPassagens > 0)
                return true;
        }
        return false;
    }

}