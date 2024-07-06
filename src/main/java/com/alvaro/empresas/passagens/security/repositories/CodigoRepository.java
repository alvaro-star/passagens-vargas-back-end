package com.alvaro.empresas.passagens.security.repositories;

import com.alvaro.empresas.passagens.security.models.temporal.CodigoVerificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CodigoRepository extends JpaRepository<CodigoVerificacao, UUID> {
    @Query("SELECT c FROM CodigoVerificacao as c WHERE c.email = :email AND c.createdAt >= :data")
    List<CodigoVerificacao> findByEmailAfterDate(@Param("email") String email, @Param("data") LocalDateTime data);
}
