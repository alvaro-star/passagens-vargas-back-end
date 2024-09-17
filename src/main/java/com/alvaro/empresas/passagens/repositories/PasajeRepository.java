package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.dtos.viajes.JPQL.PasajeJPQLBusca;
import com.alvaro.empresas.passagens.dtos.viajes.JPQL.ViajeDTOJPQLRelatorio;
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
    @Query(value = "UPDATE tb_pasaje SET pagado = :pagado WHERE fk_idtb_factura_pasaje = :idFactura", nativeQuery = true)
    void updateValuePagado(@Param("idFactura") UUID id, @Param("pagado") Boolean pagado);

    @Query(value = "SELECT n_silla FROM tb_pasaje WHERE fk_idtb_precio = :idPrecio AND pagado = true AND fk_idtb_factura_rembolso IS NULL", nativeQuery = true)
    List<Integer> getPasajesVendidosAndNoRembolso(UUID idPrecio);

    @Query("SELECT new com.alvaro.empresas.passagens.dtos.viajes.JPQL.PasajeJPQLBusca(p.salida.lugarId, p.destino.lugarId, p.nSilla, p.compradoWeb, p.facturaRembolsoId, p.enEfectivo, p.metodoPago, p.precioPagado) " +
            "FROM PasajeModel p WHERE p.precio.id = :idPrecio AND p.estaPagado = true")
    List<PasajeJPQLBusca> getPasajesPagados(UUID idPrecio);

    List<PasajeModel> findByPrecioIdAndEstaPagado(UUID idPrecio, boolean estaPago);
}
