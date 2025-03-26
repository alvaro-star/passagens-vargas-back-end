package com.alvaro.empresas.passagens.pagamentos.repositories;

import com.alvaro.empresas.passagens.pagamentos.models.FaturaReembolsoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FacturaRembolsoRepository extends JpaRepository<FaturaReembolsoModel, UUID> {
}
