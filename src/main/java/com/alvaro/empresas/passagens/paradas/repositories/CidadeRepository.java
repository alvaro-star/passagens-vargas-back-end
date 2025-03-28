package com.alvaro.empresas.passagens.paradas.repositories;

import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CidadeRepository extends JpaRepository<CidadeModel, Integer> {
    Page<CidadeModel> findByNomeContaining(String nome, Pageable pageable);

    Page<CidadeModel> findByDepartamentoId(Integer id, Pageable pageable);
}
