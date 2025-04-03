package com.alvaro.empresas.passagens.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.onibus.dtos.OnibusCreateDTO;
import com.alvaro.empresas.passagens.onibus.models.OnibusModel;
import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;

import jakarta.persistence.EntityManager;

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

    public EmpresaModel cadastrarEmpresa(String nome) {
        var empresa = new EmpresaModel(nome, "logo", "numerocuenta", true, false);
        em.persist(empresa);
        return empresa;
    }

    public OnibusModel cadastrarOnibus(String placa, EmpresaModel empresaModel) {
        var dto = new OnibusCreateDTO(placa, null);
        var onibus = new OnibusModel(dto, empresaModel);
        em.persist(onibus);
        return onibus;
    }

    public List<LugarModel> loadLugares(String[] nomes) {
        List<LugarModel> lugares = new ArrayList<>();
        for (String nome : nomes) {
            var depModel = new DepartamentoModel(nome, "SC");
            em.persist(depModel);
            var cidade = new CidadeModel(nome, depModel);
            em.persist(cidade);
            var lugar = new LugarModel("Terminal " + nome, cidade);
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

    public HashMap<String, List<OnibusModel>> laodOnibuses(HashMap<String, EmpresaModel> empresas, int nOnibuses) {
        HashMap<String, List<OnibusModel>> onibus = new HashMap<>();
        OnibusModel aux;
        int i;
        for (String key : empresas.keySet()) {
            List<OnibusModel> onibusOfEmpresa = new ArrayList<>();
            for (i = 0; i < nOnibuses; i++) {
                aux = cadastrarOnibus(key, empresas.get(key));
                onibusOfEmpresa.add(aux);
            }
            onibus.put(key, onibusOfEmpresa);
        }

        return onibus;
    }

}
