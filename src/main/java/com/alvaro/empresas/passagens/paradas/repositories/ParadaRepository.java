package com.alvaro.empresas.passagens.paradas.repositories;

import com.alvaro.empresas.passagens.enums.EnumParada;
import com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViajeBuscaDTOJPQL;
import com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViajeEmpresaDTOJPQ;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParadaRepository extends JpaRepository<ParadaModel, Integer> {
    @Query("SELECT new com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViajeBuscaDTOJPQL(s.viaje.codigo, s.viaje.empresa.logo, s, d) " +
            "FROM ParadaModel s, ParadaModel d " +
            "WHERE s.lugar.id = :id_lugar_salida " +
            "AND s.dataHora BETWEEN :data_start AND :data_end " +
            "AND s.tipo != 'DESTINO' " +
            "AND s.viaje.codigo = d.viaje.codigo " +
            "AND d.lugar.id = :id_lugar_destino " +
            "AND d.tipo != 'SALIDA'")
    List<ViajeBuscaDTOJPQL> cargarSalidasDelDia(
            @Param("id_lugar_salida") Integer idLugarSalida,
            @Param("id_lugar_destino") Integer idLugarDestino,
            @Param("data_start") LocalDateTime dataStart,
            @Param("data_end") LocalDateTime dataEnd);


    /*@Query(value = "SELECT * FROM tb_parada " +
            "WHERE fk_idtb_empresa = :id_empresa AND fk_idtb_lugar = :id_lugar " +
            "AND data_hora BETWEEN :data_start AND :data_end AND tipo != 'DESTINO'", nativeQuery = true)*/
    @Query("SELECT new com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViajeEmpresaDTOJPQ(v, s, d) " +
            "FROM ParadaModel s, ParadaModel d, ViajeModel v " +
            "WHERE s.empresa.id = :id_empresa AND s.lugar.id = :id_lugar " +
            "AND s.dataHora BETWEEN :data_start AND :data_end AND s.tipo != 'DESTINO' " +
            "AND s.viaje.codigo = d.viaje.codigo AND d.tipo = 'DESTINO' " +
            "AND v.codigo = s.viaje.codigo")
    List<ViajeEmpresaDTOJPQ> cargarSalidasDelDiaFromEmpresa2(@Param("id_empresa") UUID idEmpresa,
                                                             @Param("id_lugar") Integer idLugar,
                                                             @Param("data_start") LocalDateTime dataStart,
                                                             @Param("data_end") LocalDateTime dataEnd);

    @Query("SELECT new com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViajeEmpresaDTOJPQ(v, s, d) " +
            "FROM ParadaModel s, ParadaModel d, ViajeModel v " +
            "WHERE s.empresa.id = :id_empresa AND s.lugar.id = :id_lugar_salida " +
            "AND s.dataHora BETWEEN :data_start AND :data_end AND s.tipo != 'DESTINO' " +
            "AND s.viaje.codigo = d.viaje.codigo AND d.lugar.id = :id_lugar_destino AND d.tipo != 'SALIDA' " +
            "AND v.codigo = s.viaje.codigo")
    List<ViajeEmpresaDTOJPQ> cargarSalidasDelDiaFromEmpresa(@Param("id_empresa") UUID idEmpresa,
                                                            @Param("id_lugar_salida") Integer idLugarSalida,
                                                            @Param("id_lugar_destino") Integer idLugarDestino,
                                                            @Param("data_start") LocalDateTime dataStart,
                                                            @Param("data_end") LocalDateTime dataEnd);

    /*    @Query(value = "SELECT * FROM tb_parada " +
            "WHERE fk_idtb_empresa = :id_empresa AND fk_idtb_lugar = :id_lugar " +
            "AND data_hora BETWEEN :data_start AND :data_end", nativeQuery = true)*/
    List<ParadaModel> findByViajeCodigoAndTipo(UUID codigoViaje, EnumParada tipo);

    Optional<ParadaModel> findFirst1ByLugarId(Integer idLugar);
}
//List<LugarModel> findByCiudadId(Integer idCiudad);
    /*
        SELECT v.* FROM ViajeModel v RIGHT Join ParadaModel p where v.id = "teste"
        List<ViajeModel> viajes

     @Query(value = "SELECT * FROM tb_parada WHERE fk_idtb_lugar = :idLugar " +
            "AND fk_idtb_viaje = :codigo", nativeQuery = true)
    List<ParadaModel> nVezesViajePassa(@Param("idLugar") Integer idLugar, @Param("codigo") UUID codigoViaje);
     */