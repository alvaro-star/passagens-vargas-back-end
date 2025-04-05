package com.alvaro.empresas.passagens.paradas.repositories;

import com.alvaro.empresas.passagens.configuracoes.jpa.ICustomRepository;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParadaRepository extends JpaRepository<ParadaModel, Integer>, ICustomRepository<ParadaModel, Integer> {
    Optional<ParadaModel> findFirst1ByLugarId(Integer idLugar);

    Page<ParadaModel> findByLugarId(Integer id, Pageable pageable);
}