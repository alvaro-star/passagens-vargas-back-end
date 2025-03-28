package com.alvaro.empresas.passagens.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alvaro.empresas.passagens.models.EmpresaModel;

import jakarta.validation.constraints.NotNull;

@Repository
public interface EmpresaRepository extends JpaRepository<EmpresaModel, UUID> {
    boolean existsById(@NotNull UUID id);
}
