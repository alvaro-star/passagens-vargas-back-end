package com.alvaro.empresas.passagens.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alvaro.empresas.passagens.configuracoes.jpa.ICustomRepository;
import com.alvaro.empresas.passagens.models.EmpresaModel;

@Repository
public interface EmpresaRepository extends JpaRepository<EmpresaModel, UUID>, ICustomRepository<EmpresaModel, UUID> {
    @SuppressWarnings("null")
    boolean existsById(UUID id);
}
