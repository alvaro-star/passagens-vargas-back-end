package com.alvaro.empresas.passagens.services.validacao;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.dtos.viajes.JPQL.ViajeDTOJPQLRelatorio;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class TiempoViajeService {
    @Autowired
    private ViajeRepository viajeRepository;
    @Value("${api.viaje.max-time-viaje-day}")
    private Integer tempoMaxViajeDias;


    public boolean validarTempoMaximoViaje(LocalDateTime dataHoraSalida, LocalDateTime dataHoraDestino) {
        return dataHoraDestino.isBefore(dataHoraSalida.plusDays(tempoMaxViajeDias).plusSeconds(2));
    }

    public List<ViajeDTOJPQLRelatorio> findViajesFromEmpresa(
            EmpresaModel empresa,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return viajeRepository.findByEmpresaFinishedInInterval(empresa.getId(), dataInicio, dataFim);
    }

    public boolean existsViajesActiveFromAutobus(
            AutobusModel autobus,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            UUID viajeCodigo
    ) {
        List<ViajeModel> viajes = findViajesFromAutobusInterval(autobus.getEmpresaId(), autobus.getId(), dataInicio, dataFim);
        if (viajeCodigo == null)
            return !viajes.isEmpty();

        for (ViajeModel viajeModel : viajes)
            if (!viajeCodigo.equals(viajeModel.getCodigo()))
                return true;
        return false;
    }

    public boolean existsViajesActiveFromAutobus(
            AutobusModel autobus,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        List<ViajeModel> viajes = findViajesFromAutobusInterval(autobus.getEmpresaId(), autobus.getId(), dataInicio, dataFim);
        return !viajes.isEmpty();
    }

    private List<ViajeModel> findViajesFromAutobusInterval(UUID empresaId,
                                                           Integer autobusId,
                                                           LocalDateTime dataInicio,
                                                           LocalDateTime dataFim) {
        LocalDateTime dataInicioAlterado = dataInicio.minusDays(tempoMaxViajeDias).minusSeconds(2);
        return viajeRepository.findByAutobusInIntervalo(empresaId, autobusId, dataInicio, dataInicioAlterado, dataFim);
    }
}
