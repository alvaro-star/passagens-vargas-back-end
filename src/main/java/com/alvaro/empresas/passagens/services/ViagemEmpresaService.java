package com.alvaro.empresas.passagens.services;

import static com.alvaro.empresas.passagens.helpers.DateTimeUtils.copyLocalTimeInLocalDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.alvaro.empresas.passagens.dtos.viagens.JPQL.ViagemDTOJPQL;
import com.alvaro.empresas.passagens.dtos.viagens.ViagemSolicitacaoDTO;
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
import com.alvaro.empresas.passagens.dtos.precos.PrecoResponseDTO;
import com.alvaro.empresas.passagens.dtos.viagens.seller.ViagemUpdateDTO;
import com.alvaro.empresas.passagens.dtos.viagens.seller.ViagemCreateDTO;
import com.alvaro.empresas.passagens.dtos.viagens.seller.ViagemResponseDTO;
import com.alvaro.empresas.passagens.dtos.viagens.seller.ViagemCreateCopyDTO;
import com.alvaro.empresas.passagens.enums.TipoParada;
import com.alvaro.empresas.passagens.helpers.DateTimeUtils;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.helpers.thymeleaf.PDFThymeleaf;
import com.alvaro.empresas.passagens.helpers.thymeleaf.PassagemItemListTHModel;
import com.alvaro.empresas.passagens.configuracoes.validations.services.ValidEnabledEntities;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.onibus.models.PisoModel;
import com.alvaro.empresas.passagens.onibus.repositories.OnibusRepository;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaResponseDTO;
import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.EmpresaRepository;
import com.alvaro.empresas.passagens.repositories.PrecoRepository;
import com.alvaro.empresas.passagens.repositories.ViagemRepository;
import com.alvaro.empresas.passagens.configuracoes.validations.services.TempoViagemService;
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
    private PDFThymeleaf pdfThymeleaf;
    @Autowired
    private OnibusRepository onibusRepository;
    @Autowired
    private UserLoguedComponent userLogued;

    public ViagemModel findById(UUID id) {
        return viagemRepository.findByIdOrThr(id);
    }

    public PageOutput<ViagemResponseDTO> findAllByEmpresaBetweenDates(UUID empresaId, String mesAnalise, Pageable pageable) {

        Integer[] anoMes = DateTimeUtils.splitAnoMonth(mesAnalise);

        userLogued.validIfIsAdminOrOwnerEmpresa(empresaId);
        var empresa = empresaRepository.findByIdOrThr(empresaId);

        LocalDateTime dataInicio = DateTimeUtils.getFirstDayOfMonth(anoMes);
        LocalDateTime dataFim = DateTimeUtils.getLastDayOfMonth(anoMes);

        var page = viagemRepository.findByEmpresaAndStartInInterval(empresa.getId(), dataInicio, dataFim, pageable);

        var dtos = page.map(model -> new ViagemResponseDTO(model.viagem()));

        return new PageOutput<>(dtos);
    }

    public PageOutput<ViagemResponseDTO> findAllFromOnibus(UUID onibusId, String mesAnalise, Pageable pageable) {
        var onibus = onibusRepository.findByIdOrThr(onibusId);
        userLogued.validIfIsAdminOrOwnerEmpresa(onibus.getEmpresaId());

        Integer[] anoMes = DateTimeUtils.splitAnoMonth(mesAnalise);
        LocalDateTime dataInicio = DateTimeUtils.getFirstDayOfMonth(anoMes);
        LocalDateTime dataFim = DateTimeUtils.getLastDayOfMonth(anoMes);

        var models = viagemRepository.findByEmpresaAndOnibusAndStartInInterval(onibus.getEmpresa().getId(), onibus.getId(), dataInicio, dataFim, pageable);

        var dtos = models.map(model -> new ViagemResponseDTO(model.viagem()));
        return new PageOutput<>(dtos);
    }

    public List<ViagemResponseDTO> findViagensByDay(UUID idEmpresa, ViagemSolicitacaoDTO dto) {
        userLogued.validIfIsAdminOrOwnerEmpresa(idEmpresa);
        List<LugarModel> lugaresSaida = lugarRepository.findByCidadeId(dto.idCidadeSaida());
        if (lugaresSaida.isEmpty()) throw new EntityNotFoundException(dto.idCidadeSaida(), CidadeModel.class);

        List<LugarModel> lugaresDestino = null;
        if (dto.idCidadeDestino() != null && dto.idCidadeDestino() != 0) {
            if (dto.idCidadeDestino().equals(dto.idCidadeSaida()))
                throw new ValidationException("idDestino", "O destino não pode ser o mesmo que a saída");
            lugaresDestino = lugarRepository.findByCidadeId(dto.idCidadeDestino());
            if (lugaresDestino.isEmpty()) throw new EntityNotFoundException(dto.idCidadeDestino(), CidadeModel.class);
        }

        LocalDateTime startDay = dto.dataSaida().atTime(LocalTime.MIN);
        LocalDateTime endDay = dto.dataSaida().atTime(LocalTime.MAX);

        List<ViagemDTOJPQL> viagensDisponiveis = new ArrayList<>();

        for (LugarModel saida : lugaresSaida) {
            if (lugaresDestino != null) lugaresDestino.forEach(destino -> {
                var viagensResult = viagemRepository.findByEmpresaAndStartInInterval(idEmpresa, saida.getId(), destino.getId(), startDay, endDay);
                viagensDisponiveis.addAll(viagensResult);
            });
            else {
                List<ViagemDTOJPQL> viagensResult = viagemRepository.findByEmpresaAndStartInInterval(idEmpresa, saida.getId(), startDay, endDay);
                viagensDisponiveis.addAll(viagensResult);
            }
        }

        return viagensDisponiveis.stream().map(viagem -> {
            var paradasDTO = new ArrayList<ParadaResponseDTO>();
            paradasDTO.add(new ParadaResponseDTO(viagem.saida()));
            paradasDTO.add(new ParadaResponseDTO(viagem.destino()));
            var precosDTO = viagem.viagem().getPrecos()
                    .stream().filter(p -> !p.getCheio())
                    .map(PrecoResponseDTO::new).toList();
            return new ViagemResponseDTO(viagem.viagem(), paradasDTO, precosDTO);
        }).toList();
    }

    public byte[] getPdfFromViagem(UUID id) {
        var model = this.findById(id);
        List<PassagemItemListTHModel> passagensTh = model.getPrecos().stream().flatMap(p -> p.getPassagens().stream()).map(PassagemItemListTHModel::new).toList();

        Context context = new Context();
        context.setVariable("passagens", passagensTh);
        return pdfThymeleaf.generatePDFByTemplate("/empresa/passageirosList", context, PageSize.A4);
    }

    @Transactional
    public ViagemResponseDTO save(ViagemCreateDTO dto) {

        var onibus = onibusRepository.findByIdOrThr(dto.idOnibus());
        userLogued.validIfIsMyEmpresa(onibus.getEmpresaId());

        ValidEnabledEntities.validOnibus(onibus);
        ValidEnabledEntities.validEmpresa(onibus.getEmpresa());

        var lugarSaida = lugarRepository.findById(dto.idLugarSaida());
        if (lugarSaida.isEmpty()) throw new ValidationException("idLugarSaida", "A saida não foi encontrada");

        var lugarDestino = lugarRepository.findById(dto.idLugarDestino());
        if (lugarDestino.isEmpty()) throw new ValidationException("idLugarDestino", "O destino não foi encontrado");

        if (dto.idLugarDestino().equals(dto.idLugarSaida()))
            throw new ValidationException("idLugarDestino", "O destino não pode ser igual a saida");

        LocalDateTime dataHoraSaidaAjustada = dto.dataSaida().withSecond(0).withNano(0);
        LocalDateTime dataHoraDestino = dataHoraSaidaAjustada.plusHours(dto.tempoViagem());

        try {
            tempoViagemService.validarTempoMaximoViagem(dataHoraSaidaAjustada, dataHoraDestino);
        } catch (Exception e) {
            throw new ValidationException("dataSaida", e.getMessage());
        }

        boolean viajeInIntervalo = tempoViagemService.existsViagensActiveFromOnibus(onibus, dataHoraSaidaAjustada, dataHoraDestino);
        if (viajeInIntervalo)
            throw new RestRuntimeException(HttpStatus.CONFLICT, "O ônibus já tem um viaje em andamento nesse intervalo");

        var model = new ViagemModel(onibus, dataHoraSaidaAjustada);

        var saida = new ParadaModel(dataHoraSaidaAjustada, dto.plataforma(), TipoParada.SAIDA, lugarSaida.get(), model);
        var destino = new ParadaModel(dataHoraDestino, 0, TipoParada.DESTINO, lugarDestino.get(), model);

        List<BigDecimal> precosDTO = List.of(dto.precoPiso1(), dto.precoPiso2());

        PisoModel aux;
        for (int i = 1; i <= onibus.getPisos().size(); i++) {
            aux = onibus.getPisoByNumero(i);
            BigDecimal precoItemList = precosDTO.get(i - 1);
            if (precoItemList == null) precoItemList = precosDTO.get(i - 2);
            var preco = new PrecoModel(precoItemList, i, aux.getNAssentos());
            preco.setViagem(model);
            preco.setEmpresa(preco.getEmpresa());
            model.addPreco(preco);
        }

        viagemRepository.save(model);

        List<PrecoResponseDTO> precosSalvos = model.getPrecos().stream().map(PrecoResponseDTO::new).toList();

        model.addParada(saida);
        model.addParada(destino);
        viagemRepository.save(model);

        List<ParadaResponseDTO> paradas = new ArrayList<>();
        paradas.add(new ParadaResponseDTO(saida));
        paradas.add(new ParadaResponseDTO(destino));
        return new ViagemResponseDTO(model, paradas, precosSalvos);
    }

    @Transactional
    // Verificar se se deseja replicar uma viagem futura em uma viagem menos futura
    public ViagemModel duplicateViagem(ViagemCreateCopyDTO dto) {

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

        boolean existe = tempoViagemService.existsViagensActiveFromOnibus(viagem.getOnibus(), primeiraHoraViagem, ultimaHoraViagem);

        if (existe)
            throw new RestRuntimeException(HttpStatus.CONFLICT, "O ônibus está ocupado nessa data com outra viagem");

        viagem.getOnibus().setEmpresa(viagem.getEmpresa());
        var novaViagem = new ViagemModel(viagem.getOnibus(), primeiraHoraViagem);

        var horaPartida = primeiraHoraViagem;
        var dataHoraInicioViagemAntiga = viagem.getSaida().getDataHora();
        viagem.getParadas().forEach((parada) -> {
            var tempoViagemToParada = ChronoUnit.SECONDS.between(dataHoraInicioViagemAntiga, parada.getDataHora());
            var novaParada = new ParadaModel(horaPartida.plusSeconds(tempoViagemToParada), parada.getPlataforma(), parada.getTipo(), parada.getLugar(), novaViagem);
            novaViagem.addParada(novaParada);
        });

        viagem.getPrecos().forEach((p) -> {
            var precoAux = new PrecoModel(p.getPreco(), p.getNPiso(), viagem.getOnibus().getPisoByNumero(p.getNPiso()).getNAssentos(), novaViagem);
            novaViagem.addPreco(precoAux);
        });

        return viagemRepository.save(novaViagem);
    }

    public void update(UUID id, ViagemUpdateDTO dto) { // Validação para que a mudança seja feita

        var viagemModel = viagemRepository.findByIdOrThr(id);
        userLogued.validIfIsMyEmpresa(viagemModel.getEmpresaId());

        var model = findById(id);
        ValidEnabledEntities.validEmpresa(model.getEmpresa());
        ValidEnabledEntities.validOnibus(model.getOnibus());

        var onibus = onibusRepository.findByIdOrThr(dto.idOnibus());
        if (!onibus.getEmpresaId().equals(model.getEmpresaId()))
            throw new RestRuntimeException(HttpStatus.CONFLICT, "Este ônibus pertence a outra empresa");
        ValidEnabledEntities.validOnibus(onibus);

        boolean isOnibusOcupado = tempoViagemService.existsViagensActiveFromOnibus(onibus, model.getSaida().getDataHora(), model.getDestino().getDataHora());
        if (isOnibusOcupado) throw new ValidationException("idOnibus", "O ônibus está ocupado com outra viagem");

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
            if (nPassagens != null && nPassagens > 0) return true;
        }
        return false;
    }

}