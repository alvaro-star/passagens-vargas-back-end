package com.alvaro.empresas.passagens.lugares.repositories;

import com.alvaro.empresas.passagens.lugares.models.LugarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LugarRepository extends JpaRepository<LugarModel, Integer> {
}
