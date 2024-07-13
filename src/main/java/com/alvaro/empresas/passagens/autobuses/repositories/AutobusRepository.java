package com.alvaro.empresas.passagens.autobuses.repositories;

import com.alvaro.empresas.passagens.autobuses.dtos.ValoresArrecadadosDTO;
import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
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

    Page<AutobusModel> findByEmpresaId(UUID idEmpresa, Pageable pageable);
/*
    @Query(value = "SELECT sum(valor_arrecadado_efectivo),sum(valor_arrecadado_web) FROM tb_viaje as v where fk_idtb_autobus = :idAutobus and cobrado = false", nativeQuery = true)
    BigDecimal[] getArrecadacao(@Param("idAutobus") Integer idAutobus);*/

    @Query("SELECT new com.alvaro.empresas.passagens.autobuses.dtos.ValoresArrecadadosDTO(SUM(v.valorArrecadadoEfectivo), SUM(v.valorArrecadadoNoWeb), SUM(v.valorArrecadadoWeb)) FROM ViajeModel v WHERE v.autobus.id = :idAutobus AND v.isCobrado = false")
    ValoresArrecadadosDTO getArrecadacao(@Param("idAutobus") Integer idAutobus);

}

