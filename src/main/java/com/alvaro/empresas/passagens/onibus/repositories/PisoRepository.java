package com.alvaro.empresas.passagens.onibus.repositories;

import com.alvaro.empresas.passagens.onibus.models.PisoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PisoRepository extends JpaRepository<PisoModel, UUID> {
}
