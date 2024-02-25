package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.TrayectoModel;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ViajeRepositoryTest {

    @Autowired
    private ViajeRepository viajeRepository;
    @Autowired
    private EntityManager em;
    @Test
    @DisplayName("Deveria me listar os trajetos que saem num intervalo...")
    void getFromTrayecto() {
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