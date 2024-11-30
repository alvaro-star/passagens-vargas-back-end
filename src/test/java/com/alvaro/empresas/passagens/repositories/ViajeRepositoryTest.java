package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.enums.TypeParada;
import com.alvaro.empresas.passagens.helpers.DadosPersist;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViajeEmpresaDTOJPQ;
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
class ViajeRepositoryTest {
    private DadosPersist dadosPersist;
    private ViajeRepository viajeRepository;
    private EntityManager em;

    @Autowired
    public ViajeRepositoryTest(EntityManager em, ViajeRepository viajeRepository) {
        this.em = em;
        this.viajeRepository = viajeRepository;
        this.dadosPersist = new DadosPersist(em);
    }

    @Value("${api.viaje.max-time-viaje-day}")
    private Integer tempoMaxViajeDias;

    private final String[] lugaresName = new String[]{"Santa Cruz", "La Paz", "Cochabamba", "Oruro", "Potosí", "Tarija", "Chuquisaca", "Pando", "Beni"};

    @Test
    @DisplayName("Dado 3 viajes registrados, verifica se o metodo foi capaz de detectar dos viajes de un autobus")
    void findViajeFromAutobusInIntervalo1() {
        String[] empresasName = new String[]{"23 de Marzo", "15 de Abril"};
        var empresasModels = dadosPersist.loadEmpresas(empresasName);

        var autobusMarzo = dadosPersist.cadastrarAutobus("2023", empresasModels.get(empresasName[0]));
        var autobusAbril = dadosPersist.cadastrarAutobus("2023", empresasModels.get(empresasName[1]));
        var lugares = dadosPersist.loadLugares(lugaresName);

        var startViaje = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(15);
        var viaje1 = cadastrarViaje(startViaje, autobusAbril, lugares);
        cadastrarViaje(startViaje, autobusAbril, lugares);
        cadastrarViaje(startViaje, autobusMarzo, lugares);

        List<ViajeModel> viaje = viajeRepository.findByAutobusInIntervalo(
                empresasModels.get(empresasName[1]).getId(),
                autobusAbril.getId(),
                startViaje,
                startViaje.minusDays(tempoMaxViajeDias),
                viaje1.getDestino().getDataHora()
        );
        assertThat(viaje.isEmpty()).isEqualTo(false);
        assertThat(viaje.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Deveria mostrar un viaje que contenga un intervalo de tiempo")
    /*Sabendo que un trayecto tiene un viaje, mas muchas paradas, si quiero realizar un viaje que pase por dos
    paradas mas no por la primera ni por la ultima necessáriamente, el metodo me debe retornar este viaje*/
    void findByEmpresaIdInInterval() {
        String[] empresasName = new String[]{"23 de Marzo", "15 de Abril"};
        var empresasModels = dadosPersist.loadEmpresas(empresasName);

        var autobusMarzo = dadosPersist.cadastrarAutobus("2023", empresasModels.get(empresasName[0]));
        var autobusAbril = dadosPersist.cadastrarAutobus("2023", empresasModels.get(empresasName[1]));
        var lugares = dadosPersist.loadLugares(lugaresName);

        var startViaje = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(15);

        var viaje1 = cadastrarViaje(startViaje, autobusAbril, lugares);
        cadastrarViaje(startViaje, autobusMarzo, lugares);
        cadastrarViaje(startViaje, autobusMarzo, lugares);

        int idLugarSalida = lugares.get(1).getId();
        int idLugarDestino = lugares.get(2).getId();
        List<ViajeEmpresaDTOJPQ> viajesEncontrados = viajeRepository.findByEmpresaAndStartInInterval(
                autobusAbril.getEmpresaId(),
                idLugarSalida,
                idLugarDestino,
                startViaje,
                startViaje.plusHours(3));
        assertThat(viajesEncontrados.size()).isEqualTo(1);
        assertThat(viajesEncontrados.get(0).getViaje()).isEqualTo(viaje1);
    }

    private ViajeModel cadastrarViaje(LocalDateTime dataHoraSalida, AutobusModel autobusModel, List<LugarModel> lugares) {
        var viaje = new ViajeModel(autobusModel, dataHoraSalida);

        int contador = 0;
        viaje.addParada(new ParadaModel(dataHoraSalida.plusDays(contador), 10, TypeParada.SALIDA, lugares.get(0), viaje));
        contador++;
        for (int i = 1; i < lugares.size() - 1; i++, contador++)
            viaje.addParada(new ParadaModel(dataHoraSalida.plusSeconds(contador), 10, TypeParada.CAMINO, lugares.get(i), viaje));
        viaje.addParada(new ParadaModel(dataHoraSalida.plusDays(contador), 10, TypeParada.DESTINO, lugares.get(lugares.size() - 1), viaje));

        em.persist(viaje);
        return viaje;
    }
    /*
    @DisplayName("No deveria retornar ningun viaje en que la fecha de salida dado maior o igual que la fecha del destino")
    void getFromTrayectoCenario2() {
        var empresa = cadastrarEmpresa("23 de Abril");
        var autobus = cadastrarAutobus("2345L", empresa);
        var trayecto1 = cadastrarTrayecto(autobus);
        var trayecto2 = cadastrarTrayecto(autobus);
        var lugares = cadastrarLugares();
        var dataAtual = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(8);
        List<ParadaModel> paradas1 = new ArrayList<>();
        List<ParadaModel> paradas2 = new ArrayList<>();

        //Hay nueve lugares registrados
        int contador = 0;
        for (LugarModel lugar : lugares) {
            paradas1.add(cadastrarParada(dataAtual.plusHours(contador), lugar, trayecto1));
            paradas2.add(cadastrarParada(dataAtual.plusHours(contador), lugar, trayecto2));
            contador++;
        }

        int size = paradas1.size();
        cadastrarViaje(paradas1.get(0), paradas1.get(size - 1), trayecto1);
        cadastrarViaje(paradas2.get(0), paradas2.get(size - 1), trayecto2);

        List<ViajeModel> viajesEncontrados = viajeRepository.getFromTrayecto(trayecto1.getCodigo(), paradas1.get(5).getDataHora(), paradas1.get(2).getDataHora());
        assertThat(viajesEncontrados.size()).isEqualTo(0);
    }

    @DisplayName("No deveria retornar ningun viaje, porque el intervalo dado no esta dentro del intervalo del viaje")
    void getFromTrayectoCenario3() {
        var empresa = cadastrarEmpresa("23 de Abril");
        var autobus = cadastrarAutobus("2345L", empresa);
        var trayecto1 = cadastrarTrayecto(autobus);
        var trayecto2 = cadastrarTrayecto(autobus);
        var lugares = cadastrarLugares();
        var dataAtual = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(8);
        List<ParadaModel> paradas1 = new ArrayList<>();
        List<ParadaModel> paradas2 = new ArrayList<>();

        //Hay nueve lugares registrados
        int contador = 0;
        for (LugarModel lugar : lugares) {
            paradas1.add(cadastrarParada(dataAtual.plusHours(contador), lugar, trayecto1));
            paradas2.add(cadastrarParada(dataAtual.plusHours(contador), lugar, trayecto2));
            contador++;
        }

        int size = paradas1.size();
        cadastrarViaje(paradas1.get(0), paradas1.get(size - 1), trayecto1);
        cadastrarViaje(paradas2.get(0), paradas2.get(size - 1), trayecto2);
        var manha7 = dataAtual.withHour(7);
        List<ViajeModel> viajesEncontrados = viajeRepository.getFromTrayecto(trayecto1.getCodigo(), manha7, paradas1.get(5).getDataHora());
        assertThat(viajesEncontrados.size()).isEqualTo(0);
        viajesEncontrados = viajeRepository.getFromTrayecto(trayecto1.getCodigo(), manha7, manha7.plusHours(10));
        assertThat(viajesEncontrados.size()).isEqualTo(0);
        viajesEncontrados = viajeRepository.getFromTrayecto(trayecto1.getCodigo(), manha7.plusHours(2), paradas1.get(size - 1).getDataHora().plusHours(3));
        assertThat(viajesEncontrados.size()).isEqualTo(0);
    }*/
}