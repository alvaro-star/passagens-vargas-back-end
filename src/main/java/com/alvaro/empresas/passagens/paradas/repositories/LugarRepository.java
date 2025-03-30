package com.alvaro.empresas.passagens.paradas.repositories;

import java.util.List;

import com.alvaro.empresas.passagens.interfaces.ICustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alvaro.empresas.passagens.paradas.models.LugarModel;

@Repository
public interface LugarRepository extends JpaRepository<LugarModel, Integer>, ICustomRepository<LugarModel, Integer> {
    Page<LugarModel> findByCidadeId(Integer idCidade, Pageable pageable);

    List<LugarModel> findByCidadeId(Integer idCidade);
}