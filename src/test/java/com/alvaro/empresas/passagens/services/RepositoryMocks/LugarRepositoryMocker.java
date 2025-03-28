package com.alvaro.empresas.passagens.services.RepositoryMocks;

import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import org.springframework.stereotype.Service;

@Service
public class LugarRepositoryMocker {
    private Integer idCiudad;
    private Integer idLugar;
    private Integer idDepartamento;

    public LugarRepositoryMocker() {
        this.idDepartamento = 0;
        this.idLugar = 0;
        this.idCiudad = 0;
    }

    public int generateIdDepartamento() {
        return ++idDepartamento;
    }

    public int generateIdLugar() {
        return ++idLugar;
    }

    public int generateIdCiudad() {
        return ++idCiudad;
    }

    public DepartamentoModel generateDepartamento(String nome) {
        var departamento = new DepartamentoModel(nome, nome);
        departamento.setId(generateIdDepartamento());
        return departamento;
    }

    public CidadeModel generateCiudad(String nome, DepartamentoModel departamento) {
        var ciudad = new CidadeModel(nome, departamento);
        ciudad.setId(generateIdCiudad());
        return ciudad;
    }

    public LugarModel generateLugar(String lugarName) {
        var departamento = generateDepartamento(lugarName);
        var ciudad = generateCiudad(lugarName, departamento);
        var lugar = new LugarModel(lugarName, ciudad);
        lugar.setId(generateIdLugar());
        return lugar;
    }
}
