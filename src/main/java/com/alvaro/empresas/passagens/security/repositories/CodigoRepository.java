package com.alvaro.empresas.passagens.security.repositories;

import com.alvaro.empresas.passagens.security.models.temporal.CodigoVerificacao;
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
public interface CodigoRepository extends JpaRepository<CodigoVerificacao, UUID> {
    @Query("SELECT c FROM CodigoVerificacao as c WHERE c.email = :email AND c.criadoEm >= :data")
    List<CodigoVerificacao> findByEmailAfterDate(@Param("email") String email, @Param("data") LocalDateTime data);


    @Transactional
    @Modifying
    @Query("DELETE from CodigoVerificacao as c WHERE c.email = :email AND c.criadoEm < :data")
    int deleteAllBeforeTime(@Param("email") String email, @Param("data") LocalDateTime data);
}