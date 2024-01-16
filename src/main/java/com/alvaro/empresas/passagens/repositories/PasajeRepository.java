package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.PasajeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PasajeRepository extends JpaRepository<PasajeModel, UUID> {
    @Query(value = "UPDATE tb_pasaje SET pagado = :pagado WHERE idtb_pasaje = :id", nativeQuery = true)
    void updateValuePagado(@Param("id") UUID id, @Param("pagado") Boolean pagado);
}
