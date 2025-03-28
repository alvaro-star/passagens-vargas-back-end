package com.alvaro.empresas.passagens.services.validacao;

import com.alvaro.empresas.passagens.onibus.models.OnibusModel;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.dtos.viagens.JPQL.ViagemDTOJPQLRelatorio;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.repositories.ViagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class TempoViagemService {
    @Autowired
    private ViagemRepository viagemRepository;
    @Value("${api.viaje.max-time-viaje-day}")
    private Integer tempoMaximoViagemDias;

    public void validarTempoMaximoViagem(LocalDateTime dataHoraSaida, LocalDateTime dataHoraDestino) {
        if (!dataHoraDestino.isBefore(dataHoraSaida.plusDays(tempoMaximoViagemDias).plusSeconds(2)))
            throw new RestRuntimeException("A viagem não pode ter mais de " + tempoMaximoViagemDias + " dias");
    }

    public List<ViagemDTOJPQLRelatorio> findViagensFromEmpresa(
            EmpresaModel empresa,
            LocalDateTime dataInicio,
            LocalDateTime dataFim) {
        return viagemRepository.findByEmpresaFinishedInInterval(empresa.getId(), dataInicio, dataFim);
    }

    public boolean existsViagensActiveFromOnibus(
            OnibusModel onibus,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            UUID codigoViagem) {
        List<ViagemModel> viagens = buscarViagensDoOnibusNoIntervalo(onibus.getEmpresaId(), onibus.getId(), dataInicio,
                dataFim);
        if (codigoViagem == null)
            return !viagens.isEmpty();

        for (ViagemModel viagemModel : viagens)
            if (!codigoViagem.equals(viagemModel.getId()))
                return true;
        return false;
    }

    public boolean existsViagensActiveFromOnibus(
            OnibusModel onibus,
            LocalDateTime dataInicio,
            LocalDateTime dataFim) {
        List<ViagemModel> viagens = buscarViagensDoOnibusNoIntervalo(onibus.getEmpresaId(), onibus.getId(), dataInicio,
                dataFim);
        return !viagens.isEmpty();
    }

    private List<ViagemModel> buscarViagensDoOnibusNoIntervalo(UUID empresaId,
            UUID onibusId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim) {
        LocalDateTime dataInicioAlterado = dataInicio.minusDays(tempoMaximoViagemDias).minusSeconds(2);
        return viagemRepository.findByOnibusInIntervalo(empresaId, onibusId, dataInicio, dataInicioAlterado, dataFim);
    }
}
