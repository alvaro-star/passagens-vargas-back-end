package com.alvaro.empresas.passagens.paradas.repositories;

import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ParadaRepository extends JpaRepository<ParadaModel, Integer> {
    @Query(value = "SELECT * FROM tb_parada " +
            "WHERE fk_idtb_lugar = :id_lugar " +
            "AND data_hora BETWEEN :data_start AND :data_end", nativeQuery = true)
    List<ParadaModel> cargarSalidasDelDia(@Param("id_lugar") Integer idLugar,
                                          @Param("data_start") LocalDateTime dataStart,
                                          @Param("data_end") LocalDateTime dataEnd);

    /*
        SELECT v.* FROM ViajeModel v RIGHT Join ParadaModel p where v.id = "teste"
        List<ViajeModel> viajes
     */
    @Query(value = "SELECT * FROM tb_parada WHERE fk_idtb_lugar = :idLugar " +
            "AND fk_idtb_viaje = :codigo", nativeQuery = true)
    List<ParadaModel> nVezesViajePassa(@Param("idLugar") Integer idLugar, @Param("codigo") UUID codigoViaje);

}
