package com.alvaro.empresas.passagens.paradas.repositories;

import com.alvaro.empresas.passagens.paradas.dtos.LugarDTOList;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LugarRepository extends JpaRepository<LugarModel, Integer> {
    List<LugarModel> findByCiudadId(Integer idCiudad);
}
/*
@Query(value = "select * from tb_lugar where fk_idtb_ciudad = :idCiudad", nativeQuery = true)
    List<LugarModel> findLugaresByCiudadId23(@Param(value = "idCiudad") Integer idCiudad);
* */

