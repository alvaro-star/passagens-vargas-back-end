package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.FacturaPasajeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FacturaPasajeRepository extends JpaRepository<FacturaPasajeModel, UUID> {
    Page<FacturaPasajeModel> findByViajeCodigo(UUID viajeCodigo, Pageable pageable);
}
