package com.alvaro.empresas.passagens.services.validacao;

import com.alvaro.empresas.passagens.dtos.viajes.JPQL.ViajeDTOJPQLRelatorio;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TiempoViajeService {
    @Autowired
    private ViajeRepository viajeRepository;
    @Value("${api.viaje.max-time-viaje-day}")
    private Integer tempoMaxViajeDias;


    public boolean validarTempoMaximoViaje(LocalDateTime dataHoraSalida, LocalDateTime dataHoraDestino) {
        return dataHoraDestino.isBefore(dataHoraSalida.plusDays(tempoMaxViajeDias).plusSeconds(2));
    }

    public List<ViajeDTOJPQLRelatorio> findViajesFromEmpresa(
            UUID empresaId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        LocalDateTime dataInicioAlterado = dataInicio.minusDays(tempoMaxViajeDias).minusSeconds(2);
        return viajeRepository.findByEmpresaIdMakedInInterval(empresaId, dataInicio, dataInicioAlterado, dataFim);
    }

    public boolean existsViajesActiveFromAutobus(
            UUID empresaId,
            Integer autobusId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            UUID viajeCodigo
    ) {
        List<ViajeModel> viajes = findViajesFromAutobusInterval(empresaId, autobusId, dataInicio, dataFim);
        if (viajeCodigo == null)
            return !viajes.isEmpty();

        for (ViajeModel viajeModel : viajes)
            if (!viajeCodigo.equals(viajeModel.getCodigo()))
                return true;
        return false;
    }

    public boolean existsViajesActiveFromAutobus(
            UUID empresaId,
            Integer autobusId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        List<ViajeModel> viajes = findViajesFromAutobusInterval(empresaId, autobusId, dataInicio, dataFim);
        return !viajes.isEmpty();
    }

    private List<ViajeModel> findViajesFromAutobusInterval(UUID empresaId,
                                                           Integer autobusId,
                                                           LocalDateTime dataInicio,
                                                           LocalDateTime dataFim) {
        LocalDateTime dataInicioAlterado = dataInicio.minusDays(tempoMaxViajeDias).minusSeconds(2);
        return viajeRepository.findViajeFromAutobusInIntervalo(empresaId, autobusId, dataInicio, dataInicioAlterado, dataFim);
    }
}
