package com.alvaro.empresas.passagens.paradas.repositories;

import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LugarRepository extends JpaRepository<LugarModel, Integer> {
}
