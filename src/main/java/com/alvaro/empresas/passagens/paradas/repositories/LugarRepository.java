package com.alvaro.empresas.passagens.paradas.repositories;

import com.alvaro.empresas.passagens.paradas.dtos.LugarDTOList;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LugarRepository extends JpaRepository<LugarModel, Integer> {
    @Query("SELECT new com.alvaro.empresas.passagens.paradas.dtos.LugarDTOList(l.id, l.nombre, l.ciudad.nombre, l.ciudad.departamento.nombre) FROM LugarModel l WHERE l.nombre LIKE %:palavra% OR l.ciudad.nombre LIKE %:palavra% OR l.ciudad.departamento.nombre LIKE %:palavra%")
    List<LugarDTOList> getLugarLikeVerb(String palavra, Pageable pageable);
}
