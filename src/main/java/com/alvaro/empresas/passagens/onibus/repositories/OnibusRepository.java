package com.alvaro.empresas.passagens.onibus.repositories;

import com.alvaro.empresas.passagens.configuracoes.jpa.ICustomRepository;
import com.alvaro.empresas.passagens.onibus.models.OnibusModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OnibusRepository extends JpaRepository<OnibusModel, UUID>, ICustomRepository<OnibusModel, UUID> {
    boolean existsByPlaca(String placa);

    Page<OnibusModel> findByEmpresaId(UUID idEmpresa, Pageable pageable);
}

