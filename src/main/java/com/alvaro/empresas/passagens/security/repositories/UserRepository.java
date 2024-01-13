package com.alvaro.empresas.passagens.security.repositories;

import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UsuarioModel, String> {
    UserDetails findByLogin(String login);
}
