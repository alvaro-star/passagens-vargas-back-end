package com.alvaro.empresas.passagens.pagamentos.repositories;

import com.alvaro.empresas.passagens.pagamentos.models.FaturaPassagemModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FaturaPassagemRepository extends JpaRepository<FaturaPassagemModel, UUID> {
    Page<FaturaPassagemModel> findByViagemId(UUID viajeCodigo, Pageable pageable);
}
