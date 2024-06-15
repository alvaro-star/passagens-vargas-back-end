package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.PasajeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PasajeRepository extends JpaRepository<PasajeModel, UUID> {
    @Modifying
    @Query(value = "UPDATE tb_pasaje SET pagado = :pagado WHERE idtb_pasaje = :id", nativeQuery = true)
    void updateValuePagado(@Param("id") UUID id, @Param("pagado") Boolean pagado);

    @Query(value = "SELECT n_silla FROM tb_pasaje WHERE fk_idtb_precio = :idPrecio AND pagado = true", nativeQuery = true)
    List<Integer> getPasajesVendidos(UUID idPrecio);

    List<PasajeModel> findByPrecioIdAndEstaPagado(UUID idPrecio, boolean estaPago);
}
