package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.PrecoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrecioRepository extends JpaRepository<PrecoModel, UUID> {
    List<PrecoModel> findByViagemId(UUID codigo);

    @Query("SELECT count(p.id) FROM PassagemModel p WHERE p.preco.id = :idPrecio")
    Integer calculateNPasajes(@Param(value = "idPrecio") UUID idPrecio);
}
