package com.alvaro.empresas.passagens.services.validacao;

import com.alvaro.empresas.passagens.repositories.ViajeRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class TempoMaxViajeValidation {
    public static boolean validarTempoMaximoViaje(Integer tempoMaximoViajeDias, LocalDateTime dataHoraSalida, LocalDateTime dataHoraDestino) {
        return dataHoraDestino.isBefore(dataHoraSalida.plusDays(tempoMaximoViajeDias).plusSeconds(2));
    }

    public static boolean existViajeInActiveInIntervaloFromAutobus(ViajeRepository viajeRepository, Integer tempoMaximoViajeDias, UUID empresaId, Integer autobusId, UUID viajeCodigo, LocalDateTime dataInicio, LocalDateTime dataFim) {
        LocalDateTime dataInicioAlterado = dataInicio.minusDays(tempoMaximoViajeDias).minusSeconds(2);
        var viaje = viajeRepository.findViajeFromAutobusInIntervalo(empresaId, autobusId, dataInicio, dataInicioAlterado, dataFim, viajeCodigo);
        return viaje.isPresent();
    }
}
