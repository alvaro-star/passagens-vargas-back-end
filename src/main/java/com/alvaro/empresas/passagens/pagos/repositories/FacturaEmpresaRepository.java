package com.alvaro.empresas.passagens.pagos.repositories;

import com.alvaro.empresas.passagens.pagos.models.FacturaEmpresaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface FacturaEmpresaRepository extends JpaRepository<FacturaEmpresaModel, UUID> {

    @Query(value = "SELECT f FROM FacturaEmpresaModel f " +
            "WHERE f.empresa.id = :empresaId " +
            "AND f.inicioConteo BETWEEN :startDay AND :endDay " +
            "ORDER BY f.inicioConteo DESC " +
            "LIMIT :limit")
    List<FacturaEmpresaModel> findByEmpresaID(@Param(value = "empresaId") UUID empresaId,
                                              @Param(value = "startDay") LocalDateTime startDay,
                                              @Param(value = "endDay") LocalDateTime endDay,
                                              @Param(value = "limit") Integer limit);
}
