package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.AsientoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsientoRepository extends JpaRepository<AsientoModel, Integer> {
}
