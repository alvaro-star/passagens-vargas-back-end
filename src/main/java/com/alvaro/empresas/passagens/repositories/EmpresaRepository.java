package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.EmpresaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmpresaRepository extends JpaRepository<EmpresaModel, UUID> {
    boolean existsById(UUID id);
}
