package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.ParadaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParadaRepository extends JpaRepository<ParadaModel, Integer> {
}
