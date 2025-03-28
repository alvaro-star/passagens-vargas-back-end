package com.alvaro.empresas.passagens.paradas.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alvaro.empresas.passagens.paradas.models.LugarModel;

@Repository
public interface LugarRepository extends JpaRepository<LugarModel, Integer> {
    List<LugarModel> findByCidadeId(Integer idCidade);
}