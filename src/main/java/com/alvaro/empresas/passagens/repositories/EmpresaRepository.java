package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.EmpresaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<EmpresaModel, UUID> {
    boolean existsById(UUID id);
}
