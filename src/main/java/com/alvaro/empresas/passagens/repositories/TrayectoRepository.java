package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.TrayectoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrayectoRepository extends JpaRepository<TrayectoModel, UUID> {
}
