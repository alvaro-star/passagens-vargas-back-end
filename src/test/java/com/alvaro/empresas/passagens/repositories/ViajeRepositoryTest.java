package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.dtos.viagens.JPQL.ViagemDTOJPQL;
import com.alvaro.empresas.passagens.enums.TipoParada;
import com.alvaro.empresas.passagens.helpers.DadosPersist;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.onibus.models.OnibusModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ViagemRepositoryTest {
    private DadosPersist dadosPersist;
    private ViagemRepository viajeRepository;
    private EntityManager em;

    @Autowired
    public ViagemRepositoryTest(EntityManager em, ViagemRepository viagemRepository) {
        this.em = em;
        this.viajeRepository = viagemRepository;
        this.dadosPersist = new DadosPersist(em);
    }

    @Value("${api.viaje.max-time-viaje-day}")
    private Integer tempoMaxViagemDias;

    private final String[] lugaresName = new String[]{"Santa Cruz", "La Paz", "Cochabamba", "Oruro", "Potosí",
            "Tarija", "Chuquisaca", "Pando", "Beni"};

    @Test
    @DisplayName("Dado 3 viagens registrados, verifica se o metodo foi capaz de detectar dos viagens de un onibus")
    void findViagemFromOnibusInIntervalo1() {
        String[] empresasName = new String[]{"23 de Marzo", "15 de Abril"};
        var empresasModels = dadosPersist.loadEmpresas(empresasName);

        var onibusMarzo = dadosPersist.cadastrarOnibus("2023", empresasModels.get(empresasName[0]));
        var onibusAbril = dadosPersist.cadastrarOnibus("2023", empresasModels.get(empresasName[1]));
        var lugares = dadosPersist.loadLugares(lugaresName);

        var startViagem = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(15);
        var viaje1 = cadastrarViagem(startViagem, onibusAbril, lugares);
        cadastrarViagem(startViagem, onibusAbril, lugares);
        cadastrarViagem(startViagem, onibusMarzo, lugares);

        List<ViagemModel> viaje = viajeRepository.findByOnibusInIntervalo(
                empresasModels.get(empresasName[1]).getId(),
                onibusAbril.getId(),
                startViagem,
                startViagem.minusDays(tempoMaxViagemDias),
                viaje1.getDestino().getDataHora());
        assertThat(viaje.isEmpty()).isEqualTo(false);
        assertThat(viaje.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Deveria mostrar un viaje que contenga un intervalo de tiempo")
        /*
         * Sabendo que un trayecto tiene un viaje, mas muchas paradas, si quiero
         * realizar un viaje que pase por dos
         * paradas mas no por la primera ni por la ultima necessáriamente, el metodo me
         * debe retornar este viaje
         */
    void findByEmpresaIdInInterval() {
        String[] empresasName = new String[]{"23 de Marzo", "15 de Abril"};
        var empresasModels = dadosPersist.loadEmpresas(empresasName);

        var onibusMarzo = dadosPersist.cadastrarOnibus("2023", empresasModels.get(empresasName[0]));
        var onibusAbril = dadosPersist.cadastrarOnibus("2023", empresasModels.get(empresasName[1]));
        var lugares = dadosPersist.loadLugares(lugaresName);

        var startViagem = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(15);

        var viaje1 = cadastrarViagem(startViagem, onibusAbril, lugares);
        cadastrarViagem(startViagem, onibusMarzo, lugares);
        cadastrarViagem(startViagem, onibusMarzo, lugares);

        int idLugarSaida = lugares.get(1).getId();
        int idLugarDestino = lugares.get(2).getId();
        List<ViagemDTOJPQL> viagensEncontrados = viajeRepository.findByEmpresaAndStartInInterval(
                onibusAbril.getEmpresaId(),
                idLugarSaida,
                idLugarDestino,
                startViagem,
                startViagem.plusHours(3));
        assertThat(viagensEncontrados.size()).isEqualTo(1);
        assertThat(viagensEncontrados.get(0).viagem()).isEqualTo(viaje1);
    }

    private ViagemModel cadastrarViagem(LocalDateTime dataHoraSaida, OnibusModel onibusModel,
                                        List<LugarModel> lugares) {
        var viaje = new ViagemModel(onibusModel, dataHoraSaida);

        int contador = 0;
        viaje.addParada(
                new ParadaModel(dataHoraSaida.plusDays(contador), 10, TipoParada.SAIDA, lugares.get(0), viaje));
        contador++;
        for (int i = 1; i < lugares.size() - 1; i++, contador++)
            viaje.addParada(new ParadaModel(dataHoraSaida.plusSeconds(contador), 10, TipoParada.CAMINHO, lugares.get(i),
                    viaje));
        viaje.addParada(new ParadaModel(dataHoraSaida.plusDays(contador), 10, TipoParada.DESTINO,
                lugares.get(lugares.size() - 1), viaje));

        em.persist(viaje);
        return viaje;
    }
    /*
     * @DisplayName("No deveria retornar ningun viaje en que la fecha de saida dado maior o igual que la fecha del destino"
     * )
     * void getFromTrayectoCenario2() {
     * var empresa = cadastrarEmpresa("23 de Abril");
     * var onibus = cadastrarOnibus("2345L", empresa);
     * var trayecto1 = cadastrarTrayecto(onibus);
     * var trayecto2 = cadastrarTrayecto(onibus);
     * var lugares = cadastrarLugares();
     * var dataAtual =
     * LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(8
     * );
     * List<ParadaModel> paradas1 = new ArrayList<>();
     * List<ParadaModel> paradas2 = new ArrayList<>();
     *
     * //Hay nueve lugares registrados
     * int contador = 0;
     * for (LugarModel lugar : lugares) {
     * paradas1.add(cadastrarParada(dataAtual.plusHours(contador), lugar,
     * trayecto1));
     * paradas2.add(cadastrarParada(dataAtual.plusHours(contador), lugar,
     * trayecto2));
     * contador++;
     * }
     *
     * int size = paradas1.size();
     * cadastrarViagem(paradas1.get(0), paradas1.get(size - 1), trayecto1);
     * cadastrarViagem(paradas2.get(0), paradas2.get(size - 1), trayecto2);
     *
     * List<ViagemModel> viagensEncontrados =
     * viajeRepository.getFromTrayecto(trayecto1.getCodigo(),
     * paradas1.get(5).getDataHora(), paradas1.get(2).getDataHora());
     * assertThat(viagensEncontrados.size()).isEqualTo(0);
     * }
     *
     * @DisplayName("No deveria retornar ningun viaje, porque el intervalo dado no esta dentro del intervalo del viaje"
     * )
     * void getFromTrayectoCenario3() {
     * var empresa = cadastrarEmpresa("23 de Abril");
     * var onibus = cadastrarOnibus("2345L", empresa);
     * var trayecto1 = cadastrarTrayecto(onibus);
     * var trayecto2 = cadastrarTrayecto(onibus);
     * var lugares = cadastrarLugares();
     * var dataAtual =
     * LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(8
     * );
     * List<ParadaModel> paradas1 = new ArrayList<>();
     * List<ParadaModel> paradas2 = new ArrayList<>();
     *
     * //Hay nueve lugares registrados
     * int contador = 0;
     * for (LugarModel lugar : lugares) {
     * paradas1.add(cadastrarParada(dataAtual.plusHours(contador), lugar,
     * trayecto1));
     * paradas2.add(cadastrarParada(dataAtual.plusHours(contador), lugar,
     * trayecto2));
     * contador++;
     * }
     *
     * int size = paradas1.size();
     * cadastrarViagem(paradas1.get(0), paradas1.get(size - 1), trayecto1);
     * cadastrarViagem(paradas2.get(0), paradas2.get(size - 1), trayecto2);
     * var manha7 = dataAtual.withHour(7);
     * List<ViagemModel> viagensEncontrados =
     * viajeRepository.getFromTrayecto(trayecto1.getCodigo(), manha7,
     * paradas1.get(5).getDataHora());
     * assertThat(viagensEncontrados.size()).isEqualTo(0);
     * viagensEncontrados = viajeRepository.getFromTrayecto(trayecto1.getCodigo(),
     * manha7, manha7.plusHours(10));
     * assertThat(viagensEncontrados.size()).isEqualTo(0);
     * viagensEncontrados = viajeRepository.getFromTrayecto(trayecto1.getCodigo(),
     * manha7.plusHours(2), paradas1.get(size - 1).getDataHora().plusHours(3));
     * assertThat(viagensEncontrados.size()).isEqualTo(0);
     * }
     */
}