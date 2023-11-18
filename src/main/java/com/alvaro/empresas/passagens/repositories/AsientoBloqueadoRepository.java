package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.AsientoBloqueadoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsientoBloqueadoRepository extends JpaRepository<AsientoBloqueadoModel, Integer> {
}
