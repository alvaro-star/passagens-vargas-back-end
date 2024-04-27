package com.alvaro.empresas.passagens.paradas.repositories;

import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CiudadRepository extends JpaRepository<CiudadModel, Integer> {
    Page<CiudadModel> findByNombreContaining(String nombre, Pageable pageable);
}
