package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.enums.EnumParada;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ViajeRepositoryTest {
    @Autowired
    private ViajeRepository viajeRepository;
    @Autowired
    private EntityManager em;
    @Value("${api.viaje.max-time-viaje-day}")
    private Integer tempoMaxViajeDias;

    @Test
    @DisplayName("Este teste verifica se o metodo pode retornar dois viajes que esten en el mismo intervalo de tiempo")
    void getFromTrayectoCenario1() {
        var empresa = cadastrarEmpresa("23 de Abril");
        var autobus = cadastrarAutobus("2345L", empresa);
        var dataInicioViaje = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(15);
        var viaje1 = cadastrarViaje(dataInicioViaje, autobus, empresa);
        var viaje2 = cadastrarViaje(dataInicioViaje, autobus, empresa);
        var lugares = cadastrarLugares();

        List<ParadaModel> paradas1 = new ArrayList<>();
        List<ParadaModel> paradas2 = new ArrayList<>();
        //Hay nueve lugares registrados
        int finalLista;
        int contador = 0;
        paradas1.add(cadastrarParada(dataInicioViaje.plusHours(contador), lugares.get(0), EnumParada.SALIDA, viaje1, empresa));
        paradas2.add(cadastrarParada(dataInicioViaje.plusHours(contador), lugares.get(0), EnumParada.SALIDA, viaje2, empresa));
        contador++;
        for (int i = 1; i < lugares.size() - 1; i++) {
            paradas1.add(cadastrarParada(dataInicioViaje.plusHours(contador), lugares.get(i), EnumParada.CAMINO, viaje1, empresa));
            paradas2.add(cadastrarParada(dataInicioViaje.plusHours(contador), lugares.get(i), EnumParada.CAMINO, viaje2, empresa));
            contador++;
        }
        paradas1.add(cadastrarParada(dataInicioViaje.plusHours(contador), lugares.get(lugares.size() - 1), EnumParada.DESTINO, viaje1, empresa));
        paradas2.add(cadastrarParada(dataInicioViaje.plusHours(contador), lugares.get(lugares.size() - 1), EnumParada.DESTINO, viaje2, empresa));
        contador++;

        List<ViajeModel> viaje = viajeRepository.findViajeFromAutobusInIntervalo(
                empresa.getId(), autobus.getId(), dataInicioViaje,
                dataInicioViaje.minusDays(tempoMaxViajeDias), dataInicioViaje.plusHours(contador - 1));
        assertThat(viaje.isEmpty()).isEqualTo(false);
        assertThat(viaje.size()).isEqualTo(2);
    }
    /*
    @DisplayName("Deveria mostrar un viaje que contenga un intervalo de tiempo")
    *Sabendo que un trayecto tiene un viaje, mas muchas paradas, si quiero realizar un viaje que pase por dos
    paradas mas no por la primera ni por la ultima necessáriamente, el viaje deveria contener el itervalo de un trayecto dado*
    void getFromTrayectoCenario1() {
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
        var viaje1 = cadastrarViaje(paradas1.get(0), paradas1.get(size - 1), trayecto1);
        var viaje2 = cadastrarViaje(paradas2.get(0), paradas2.get(size - 1), trayecto2);

        List<ViajeModel> viajesEncontrados = viajeRepository.getFromTrayecto(trayecto1.getCodigo(), paradas1.get(3).getDataHora(), paradas1.get(5).getDataHora());
        assertThat(viajesEncontrados.size()).isEqualTo(1);
        assertThat(viajesEncontrados.get(0)).isEqualTo(viaje1);
    }

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

    private EmpresaModel cadastrarEmpresa(String nombre) {
        var empresa = new EmpresaModel(nombre, "logo", "numerocuenta", true, false);
        em.persist(empresa);
        return empresa;
    }

    private AutobusModel cadastrarAutobus(String placa, EmpresaModel empresaModel) {
        var autobus = new AutobusModel(placa, true, empresaModel);
        em.persist(autobus);
        return autobus;
    }

    private ViajeModel cadastrarViaje(LocalDateTime dataHoraSalida, AutobusModel autobusModel, EmpresaModel empresaModel) {
        var viaje = new ViajeModel(dataHoraSalida, autobusModel, empresaModel);
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

    private ParadaModel cadastrarParada(LocalDateTime data, LugarModel lugar, EnumParada tipo, ViajeModel viaje, EmpresaModel empresaModel) {
        var parada = new ParadaModel(data, 10, tipo, lugar, viaje, empresaModel);
        em.persist(parada);
        return parada;
    }
}