package com.alvaro.empresas.passagens.paradas.repositories;

import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CiudadRepository extends JpaRepository<CiudadModel, Integer> {
}
