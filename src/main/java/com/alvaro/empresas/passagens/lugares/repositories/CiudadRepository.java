package com.alvaro.empresas.passagens.lugares.repositories;

import com.alvaro.empresas.passagens.lugares.models.CiudadModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CiudadRepository extends JpaRepository<CiudadModel, Integer> {
}
