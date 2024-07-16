package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.PrecioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrecioRepository extends JpaRepository<PrecioModel, UUID> {
    List<PrecioModel> findByViajeCodigo(UUID codigo);

    @Query("SELECT count(p.id) FROM PasajeModel p WHERE p.precio.id = :idPrecio")
    Integer calculateNPasajes(@Param(value = "idPrecio") UUID idPrecio);
}
