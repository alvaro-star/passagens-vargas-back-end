package com.alvaro.empresas.passagens.paradas.repositories;

import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartamentoRepository extends JpaRepository<DepartamentoModel, Integer> {
}
