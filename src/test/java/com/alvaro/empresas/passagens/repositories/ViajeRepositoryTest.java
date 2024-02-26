package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.TrayectoModel;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
@DataJpaTest
class ViajeRepositoryTest {
    @Autowired
    private ViajeRepository viajeRepository;
    @Autowired
    private EntityManager em;
    @Test
    @DisplayName("Deveria mostrar todos los viajes que contengan un intervalo dentro de tiempo dado")
    /*
    Sabendo que un trayecto tiene un viaje, mas muchas paradas, si quiero realizar un viaje que pase por dos
    paradas mas no por la primera ni por la ultima necessáriamente, el viaje deveria contener el itervalo de un trayecto dado
    * */
    void getFromTrayectoCenario1() {

    }

    private void cadastrarEmpresa(String nombre){
        em.persist(new EmpresaModel(nombre, "logo", "numerocuenta"));
    }

    private void cadastrarAutobus(String placa, EmpresaModel empresaModel){
        em.persist(new AutobusModel(placa, empresaModel));
    }
    private void cadastrarTrayecto(AutobusModel autobusModel){
        em.persist(new TrayectoModel(autobusModel));
    }
}