package com.alvaro.empresas.passagens.services.RepositoryMocks;

import com.alvaro.empresas.passagens.onibus.models.OnibusModel;
import com.alvaro.empresas.passagens.enums.TipoParada;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ViagemRepositoryMock {
    private Integer lastIdParada;

    public ViagemRepositoryMock() {
        this.lastIdParada = 0;
    }

    public Integer gerarIdParada() {
        return ++lastIdParada;
    }

    public ParadaModel newParada(LocalDateTime fechaPartida, TipoParada tipo, LugarModel lugar, ViagemModel viaje) {
        var parada = new ParadaModel(fechaPartida, 10, tipo, lugar, viaje);
        parada.setId(gerarIdParada());
        return parada;
    }

    public ViagemModel createViagem(OnibusModel onibus, LocalDateTime dataInicio, List<LugarModel> lugares,
            int diffDias) {
        if (lugares.size() < 2)
            throw new ArrayIndexOutOfBoundsException("O numero de elementos de lugares eh menor que 2");
        var viaje = new ViagemModel(onibus, dataInicio);
        viaje.setId(UUID.randomUUID());
        var fechaPartida = dataInicio;
        viaje.addParada(newParada(dataInicio, TipoParada.SAIDA, lugares.get(0), viaje));

        for (int i = 1; i < lugares.size() - 1; i++) {
            fechaPartida = dataInicio.plusHours(i * diffDias);
            var lugarParada = lugares.get(i);
            viaje.addParada(newParada(fechaPartida, TipoParada.CAMINHO, lugarParada, viaje));
        }
        fechaPartida = fechaPartida.plusHours(diffDias);

        var lugarParada = lugares.get(lugares.size() - 1);
        viaje.addParada(newParada(fechaPartida, TipoParada.DESTINO, lugarParada, viaje));
        return viaje;
    }
}
