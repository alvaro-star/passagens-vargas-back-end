package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.SillaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SillaRepository extends JpaRepository<SillaModel, UUID> {
}
