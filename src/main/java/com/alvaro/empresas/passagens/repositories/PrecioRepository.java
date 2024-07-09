package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.PrecioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrecioRepository extends JpaRepository<PrecioModel, UUID> {
    List<PrecioModel> findByViajeCodigo(UUID codigo);
}
