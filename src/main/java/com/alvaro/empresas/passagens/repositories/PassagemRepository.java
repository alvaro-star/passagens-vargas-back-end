package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.dtos.viagens.JPQL.PassagemJPQLBusca;
import com.alvaro.empresas.passagens.interfaces.ICustomRepository;
import com.alvaro.empresas.passagens.models.PassagemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PassagemRepository extends JpaRepository<PassagemModel, UUID>, ICustomRepository<PassagemModel, UUID> {
    @Modifying
    @Query(value = "UPDATE tb_passagem SET pago = :pago WHERE fk_idtb_fatura_passagem = :idFatura", nativeQuery = true)
    void updateValuePagado(@Param("idFatura") UUID id, @Param("pago") Boolean pago);

    @Query(value = "SELECT n_assento FROM tb_passagem WHERE fk_idtb_preco = :idPreco AND pago = true AND fk_idtb_fatura_reembolso IS NULL", nativeQuery = true)
    List<Integer> getPassagensVendidasENaoReembolsadas(UUID idPreco);

    @Query("SELECT new com.alvaro.empresas.passagens.dtos.viagens.JPQL.PassagemJPQLBusca(p.saida.lugarId, p.destino.lugarId, p.nAssento, p.compradoWeb, p.faturaReembolsoId, p.emDinheiro, p.metodoPagamento, p.precoPago) " +
            "FROM PassagemModel p WHERE p.preco.id = :idPreco AND p.estaPago = true")
    List<PassagemJPQLBusca> getPassagensPagas(UUID idPreco);

    List<PassagemModel> findByPrecoIdAndEstaPago(UUID idPreco, boolean estaPago);
}