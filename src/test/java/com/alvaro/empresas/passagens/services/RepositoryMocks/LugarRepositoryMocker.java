package com.alvaro.empresas.passagens.services.RepositoryMocks;

import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import org.springframework.stereotype.Service;

@Service
public class LugarRepositoryMocker {
    private Integer idCidade;
    private Integer idLugar;
    private Integer idDepartamento;

    public LugarRepositoryMocker() {
        this.idDepartamento = 0;
        this.idLugar = 0;
        this.idCidade = 0;
    }

    public int generateIdDepartamento() {
        return ++idDepartamento;
    }

    public int generateIdLugar() {
        return ++idLugar;
    }

    public int generateIdCidade() {
        return ++idCidade;
    }

    public DepartamentoModel generateDepartamento(String nome) {
        var departamento = new DepartamentoModel(nome, nome);
        departamento.setId(generateIdDepartamento());
        return departamento;
    }

    public CidadeModel generateCidade(String nome, DepartamentoModel departamento) {
        var cidade = new CidadeModel(nome, departamento);
        cidade.setId(generateIdCidade());
        return cidade;
    }

    public LugarModel generateLugar(String lugarName) {
        var departamento = generateDepartamento(lugarName);
        var cidade = generateCidade(lugarName, departamento);
        var lugar = new LugarModel(lugarName, cidade);
        lugar.setId(generateIdLugar());
        return lugar;
    }
}
