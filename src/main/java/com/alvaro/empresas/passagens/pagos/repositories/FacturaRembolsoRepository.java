package com.alvaro.empresas.passagens.pagos.repositories;

import com.alvaro.empresas.passagens.pagos.models.FacturaRembolsoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FacturaRembolsoRepository extends JpaRepository<FacturaRembolsoModel, UUID> {
}
