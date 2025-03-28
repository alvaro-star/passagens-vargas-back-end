package com.alvaro.empresas.passagens.security.repositories;

import com.alvaro.empresas.passagens.enums.TipoSolicitacao;
import com.alvaro.empresas.passagens.security.models.UsuarioSolicitacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UsuarioSolicitacaoRepository extends JpaRepository<UsuarioSolicitacaoModel, UUID> {
    @Query("SELECT u FROM UsuarioSolicitacaoModel u WHERE u.email=:email AND u.createdAt >= :criadoEm AND u.tipo = :tipo ORDER BY u.createdAt DESC")
    List<UsuarioSolicitacaoModel> findByEmailAfterTime(
            @Param("email") String email,
            @Param("criadoEm") LocalDateTime criadoEm,
            @Param("tipo") TipoSolicitacao tipoOperacao);

    @Modifying
    @Transactional
    @Query("DELETE FROM UsuarioSolicitacaoModel u WHERE u.email = :email AND u.createdAt < :criadoEm")
    void deleteByEmailBeforeTime(@Param("email") String email, @Param("criadoEm") LocalDateTime criadoEm);
}