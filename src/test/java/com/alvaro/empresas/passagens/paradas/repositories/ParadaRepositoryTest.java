package com.alvaro.empresas.passagens.paradas.repositories;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ParadaRepositoryTest {
    @Autowired
    private ParadaRepository paradaRepository;
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("Dado tres registros de parada, deveriam ser mostradas os codigos de tres trayectos que posseen una parada de un lugar em um dia determinado")
    void cargarSalidasDelDiaCenario1() {
        var empresa = cadastrarEmpresa("23 de Abril");
        var autobus = cadastrarAutobus("2345L", empresa);
        var viaje1 = cadastrarViaje(autobus);
        var viaje2 = cadastrarViaje(autobus);
        var viaje3 = cadastrarViaje(autobus);
        var viaje4 = cadastrarViaje(autobus);
        var lugares = cadastrarLugares();
        var dataAtual = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(8);
        List<ParadaModel> paradas1 = new ArrayList<>();
        List<ParadaModel> paradas2 = new ArrayList<>();
        List<ParadaModel> paradas3 = new ArrayList<>();
        List<ParadaModel> paradas4 = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            paradas1.add(cadastrarParada(dataAtual.plusDays(i), lugares.get(i), viaje1));
            paradas2.add(cadastrarParada(dataAtual.plusDays(i), lugares.get(i), viaje2));
            paradas3.add(cadastrarParada(dataAtual.plusDays(i), lugares.get(i), viaje3));
            paradas4.add(cadastrarParada(dataAtual.plusDays(i), lugares.get(i + 1), viaje4));
        }

        LocalDateTime startDay = dataAtual.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDay = startDay.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        List<byte[]> trayectosDia = paradaRepository.cargarSalidasDelDia(lugares.get(1).getId(), startDay, endDay);
        assertThat(trayectosDia.size()).isEqualTo(3);

        trayectosDia = paradaRepository.cargarSalidasDelDia(lugares.get(7).getId(), startDay, endDay);
        assertThat(trayectosDia.size()).isEqualTo(0);
    }


    private EmpresaModel cadastrarEmpresa(String nombre) {
        var empresa = new EmpresaModel(nombre, "logo", "numerocuenta");
        em.persist(empresa);
        return empresa;
    }

    private AutobusModel cadastrarAutobus(String placa, EmpresaModel empresaModel) {
        var autobus = new AutobusModel(placa, empresaModel);
        em.persist(autobus);
        return autobus;
    }

    private ViajeModel cadastrarViaje(AutobusModel autobusModel) {
        var viaje = new ViajeModel(autobusModel);
        em.persist(viaje);
        return viaje;
    }

    private List<LugarModel> cadastrarLugares() {
        List<String> nombresDepartamentos = Arrays.asList("Santa Cruz", "La Paz", "Cochabamba", "Oruro", "Potosí", "Tarija", "Chuquisaca", "Pando", "Beni");
        List<LugarModel> lugares = new ArrayList<>();
        for (String nombreDepartamento : nombresDepartamentos) {
            var depModel = new DepartamentoModel(nombreDepartamento, "SC");
            em.persist(depModel);
            var ciudad = new CiudadModel(nombreDepartamento, depModel);
            em.persist(ciudad);
            var lugar = new LugarModel("Terminal " + nombreDepartamento, ciudad);
            em.persist(lugar);
            lugares.add(lugar);
        }
        return lugares;
    }

    private ParadaModel cadastrarParada(LocalDateTime data, LugarModel lugar, ViajeModel viaje) {
        var parada = new ParadaModel(data, 10, lugar, viaje);
        em.persist(parada);
        return parada;
    }
}