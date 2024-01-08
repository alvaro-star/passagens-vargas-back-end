package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.dtos.TrayectoDTO;
import com.alvaro.empresas.passagens.models.TrayectoModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrayectoRepository extends JpaRepository<TrayectoModel, UUID> {
}
