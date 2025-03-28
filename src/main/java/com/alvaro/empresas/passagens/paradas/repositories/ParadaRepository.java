package com.alvaro.empresas.passagens.paradas.repositories;

import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParadaRepository extends JpaRepository<ParadaModel, Integer> {
    Optional<ParadaModel> findFirst1ByLugarId(Integer idLugar);
}