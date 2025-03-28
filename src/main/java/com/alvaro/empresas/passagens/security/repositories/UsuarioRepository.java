package com.alvaro.empresas.passagens.security.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alvaro.empresas.passagens.security.models.UsuarioModel;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, UUID> {

    @Query("SELECT u FROM UsuarioModel as u WHERE u.email = :email")
    Optional<UsuarioModel> findByEmail(String email);

    Boolean existsByEmail(String email);

    Page<UsuarioModel> findByEmpresaId(UUID idEmpresa, Pageable pageable);
}
