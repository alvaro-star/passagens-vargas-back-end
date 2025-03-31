package com.alvaro.empresas.passagens.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alvaro.empresas.passagens.interfaces.ICustomRepository;
import com.alvaro.empresas.passagens.models.PrecoModel;

@Repository
public interface PrecoRepository extends JpaRepository<PrecoModel, UUID>, ICustomRepository<PrecoModel, UUID> {
    List<PrecoModel> findByViagemId(UUID id);

    @Query("SELECT count(p.id) FROM PassagemModel p WHERE p.preco.id = :idPreco")
    Integer calcularNPassagens(@Param(value = "idPreco") UUID idPreco);
}