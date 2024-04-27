package com.alvaro.empresas.passagens.autobuses.repositories;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface AutobusRepository extends JpaRepository<AutobusModel, Integer> {
    boolean existsByPlaca(String placa);

    Page<AutobusModel> findByEmpresa(EmpresaModel empresaModel, Pageable pageable);

    @Query(value = "SELECT sum(valorArrecadado) FROM tb_viaje as v where fk_idtb_autobus = :idAutobus and cobrado = false", nativeQuery = true)
    BigDecimal getArrecadacao(@Param("idAutobus") Integer idAutobus);
}
