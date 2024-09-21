package com.alvaro.empresas.passagens.security.repositories;

import com.alvaro.empresas.passagens.enums.EnumTypeSolicitudOperation;
import com.alvaro.empresas.passagens.security.models.UsuarioSolicitudModel;
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
public interface UsuarioSolicitudRepository extends JpaRepository<UsuarioSolicitudModel, UUID> {
    @Query("SELECT u FROM UsuarioSolicitudModel u WHERE u.email=:email AND u.createdAt >= :createdAt AND u.tipo = :tipo ORDER BY u.createdAt DESC")
    List<UsuarioSolicitudModel> findByEmailAfterTime(
            @Param("email") String email,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("tipo") EnumTypeSolicitudOperation tipoOperacao);

    @Modifying
    @Transactional
    @Query("DELETE FROM UsuarioSolicitudModel u WHERE u.email = :email AND u.createdAt < :createdAt")
    void deleteByEmailBeforeTime(@Param("email") String email, @Param("createdAt") LocalDateTime createdAt);
}
