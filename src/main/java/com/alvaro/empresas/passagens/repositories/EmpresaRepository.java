package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.EmpresaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface EmpresaRepository extends JpaRepository<EmpresaModel, UUID> {
    boolean existsById(UUID id);

    @Query(value = "SELECT sum(valorArrecadado) FROM tb_viaje as v where fk_idtb_empresa = :idEmpresa and cobrado = false", nativeQuery = true)
    BigDecimal getArrecadacao(@Param("idEmpresa") UUID idEmpresa);
}
