package com.alvaro.empresas.passagens.helpers;

import com.alvaro.empresas.passagens.onibus.models.AutobusModel;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DadosPersist {
    private EntityManager em;

    public DadosPersist(EntityManager em) {
        this.em = em;
    }

    public Pageable makePageable() {
        return PageRequest.of(0, 10);
    }

    public Pageable makePageable(int pageNumber) {
        return PageRequest.of(pageNumber, 10);
    }

    public Pageable makePageable(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize);
    }

    public EmpresaModel cadastrarEmpresa(String nombre) {
        var empresa = new EmpresaModel(nombre, "logo", "numerocuenta", true, false);
        em.persist(empresa);
        return empresa;
    }

    public AutobusModel cadastrarAutobus(String placa, EmpresaModel empresaModel) {
        var autobus = new AutobusModel(placa, true, empresaModel);
        em.persist(autobus);
        return autobus;
    }

    public List<LugarModel> loadLugares(String[] nomes) {
        List<LugarModel> lugares = new ArrayList<>();
        for (String nome : nomes) {
            var depModel = new DepartamentoModel(nome, "SC");
            em.persist(depModel);
            var ciudad = new CiudadModel(nome, depModel);
            em.persist(ciudad);
            var lugar = new LugarModel("Terminal " + nome, ciudad);
            em.persist(lugar);
            lugares.add(lugar);
        }
        return lugares;
    }

    public HashMap<String, EmpresaModel> loadEmpresas(String[] empresaNames) {
        HashMap<String, EmpresaModel> models = new HashMap<>();
        EmpresaModel aux;
        for (String name : empresaNames) {
            aux = cadastrarEmpresa(name);
            models.put(name, aux);
        }
        return models;
    }

    public HashMap<String, List<AutobusModel>> laodAutobuses(HashMap<String, EmpresaModel> empresas, int nAutobuses) {
        HashMap<String, List<AutobusModel>> autobuses = new HashMap<>();
        AutobusModel aux;
        int i;
        for (String key : empresas.keySet()) {
            List<AutobusModel> autobusOfEmpresa = new ArrayList();
            for (i = 0; i < nAutobuses; i++) {
                aux = cadastrarAutobus(key, empresas.get(key));
                autobusOfEmpresa.add(aux);
            }
            autobuses.put(key, autobusOfEmpresa);
        }

        return autobuses;
    }


}
