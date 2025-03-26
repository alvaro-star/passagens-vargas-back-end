package com.alvaro.empresas.passagens.onibus.repositories;

import com.alvaro.empresas.passagens.onibus.models.PisoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PisoRepository extends JpaRepository<PisoModel, Integer> {
}
