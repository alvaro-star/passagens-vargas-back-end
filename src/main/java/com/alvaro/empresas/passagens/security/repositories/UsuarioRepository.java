package com.alvaro.empresas.passagens.security.repositories;

import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, UUID> {
    UserDetails findByLogin(String login);

    @Query("SELECT u FROM UsuarioModel as u WHERE u.login = :email")
    Optional<UsuarioModel> findByEmail(String email);

    Boolean existsByLogin(String email);

    Page<UsuarioModel> findByIdEmpresa(UUID idEmpresa, Pageable pageable);
}
