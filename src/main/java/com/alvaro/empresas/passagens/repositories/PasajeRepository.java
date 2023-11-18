package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.PasajeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasajeRepository extends JpaRepository<PasajeModel, Integer> {
}
