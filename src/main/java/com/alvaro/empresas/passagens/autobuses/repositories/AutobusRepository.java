package com.alvaro.empresas.passagens.autobuses.repositories;

import com.alvaro.empresas.passagens.autobuses.dtos.ValoresArrecadadosDTO;
import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AutobusRepository extends JpaRepository<AutobusModel, Integer> {
    boolean existsByPlaca(String placa);

    Page<AutobusModel> findByEmpresaId(UUID idEmpresa, Pageable pageable);

    @Query("SELECT new com.alvaro.empresas.passagens.autobuses.dtos.ValoresArrecadadosDTO(SUM(v.valorArrecadadoEfectivo), SUM(v.valorArrecadadoNoWeb), SUM(v.valorArrecadadoWeb)) FROM ViajeModel v WHERE v.autobus.id = :idAutobus AND v.isCobrado = false")
    ValoresArrecadadosDTO getArrecadacao(@Param("idAutobus") Integer idAutobus);

}

