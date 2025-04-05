package com.alvaro.empresas.passagens.pagamentos.repositories;

import com.alvaro.empresas.passagens.configuracoes.jpa.ICustomRepository;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaEmpresaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
public interface FaturaEmpresaRepository extends JpaRepository<FaturaEmpresaModel, UUID>, ICustomRepository<FaturaEmpresaModel, UUID> {

    @Query(value = "SELECT f FROM FaturaEmpresaModel f " +
            "WHERE f.empresa.id = :empresaId " +
            "AND f.inicioContagem BETWEEN :dataInicio AND :dataFim " +
            "ORDER BY f.inicioContagem DESC " +
            "LIMIT :limite")
    List<FaturaEmpresaModel> findByEmpresaId(@Param(value = "empresaId") UUID empresaId,
                                             @Param(value = "dataInicio") LocalDateTime dataInicio,
                                             @Param(value = "dataFim") LocalDateTime dataFim,
                                             @Param(value = "limite") Integer limite);
}