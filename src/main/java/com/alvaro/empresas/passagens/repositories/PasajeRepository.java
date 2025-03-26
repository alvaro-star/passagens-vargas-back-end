package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.dtos.viajes.JPQL.PasajeJPQLBusca;
import com.alvaro.empresas.passagens.models.PassagemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PasajeRepository extends JpaRepository<PassagemModel, UUID> {
    @Modifying
    @Query(value = "UPDATE tb_pasaje SET pagado = :pagado WHERE fk_idtb_factura_pasaje = :idFactura", nativeQuery = true)
    void updateValuePagado(@Param("idFactura") UUID id, @Param("pagado") Boolean pagado);

    @Query(value = "SELECT n_silla FROM tb_pasaje WHERE fk_idtb_preco = :idPreco AND pagado = true AND fk_idtb_factura_rembolso IS NULL", nativeQuery = true)
    List<Integer> getPasajesVendidosAndNoRembolso(UUID idPreco);

    @Query("SELECT new com.alvaro.empresas.passagens.dtos.viajes.JPQL.PasajeJPQLBusca(p.saida.lugarId, p.destino.lugarId, p.numeroAssento, p.compradoWeb, p.faturaReembolsoId, p.emDinheiro, p.metodoPagamento, p.precoPago) " +
            "FROM PassagemModel p WHERE p.preco.id = :idPreco AND p.estaPago = true")
    List<PasajeJPQLBusca> getPasajesPagados(UUID idPreco);

    List<PassagemModel> findByPrecoIdAndEstaPago(UUID idPecio, boolean estaPago);
}
