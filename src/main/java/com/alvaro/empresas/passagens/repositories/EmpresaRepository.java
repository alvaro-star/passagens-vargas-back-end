package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.EmpresaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmpresaRepository extends JpaRepository<EmpresaModel, UUID> {
    boolean existsById(UUID id);

    //@Query("SELECT new com.alvaro.empresas.passagens.autobuses.dtos.ValoresArrecadadosDTO(SUM(v.valorArrecadadoEfectivo), SUM(v.valorArrecadadoNoWeb), SUM(v.valorArrecadadoWeb)) FROM ViajeModel v WHERE v.empresa.id = :idEmpresa AND v.isCobrado = false")
    //ValoresArrecadadosDTO getArrecadacao(@Param("idEmpresa") UUID idEmpresa);
}
