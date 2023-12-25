package com.alvaro.empresas.passagens.lugares.repositories;

import com.alvaro.empresas.passagens.lugares.models.DepartamentoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartamentoRepository extends JpaRepository<DepartamentoModel, Integer> {
}
