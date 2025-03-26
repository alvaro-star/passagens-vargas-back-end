package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.dtos.viajes.JPQL.ViajeDTOJPQL;
import com.alvaro.empresas.passagens.dtos.viajes.JPQL.ViajeDTOJPQLRelatorio;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViajeBuscaDTOJPQL;
import com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViajeEmpresaDTOJPQ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ViajeRepository extends JpaRepository<ViagemModel, UUID> {
    Page<ViagemModel> findByEmpresaId(UUID id, Pageable pageable);

    @Query("SELECT v FROM ViagemModel v WHERE v.empresa.id = :empresaId AND v.dataHoraSalida >= :dataHoraSalida")
    Page<ViagemModel> findAfterDate(UUID empresaId, LocalDateTime dataHoraSalida, Pageable pageable);


    @Query("SELECT new com.alvaro.empresas.passagens.dtos.viajes.JPQL.ViajeDTOJPQL(v, s, d) " +
            "FROM ViagemModel v, ParadaModel s, ParadaModel d " +
            "WHERE v.empresa.id = :empresaId " +
            "AND v.dataHoraSalida BETWEEN :dataInicio AND :dataFim " +
            "AND s.viagem.id = v.id AND s.tipo = 'SALIDA' " +
            "AND d.viagem.id = v.id AND d.tipo = 'DESTINO'")
    Page<ViajeDTOJPQL> findByEmpresaAndStartInInterval(UUID empresaId,
                                                       LocalDateTime dataInicio,
                                                       LocalDateTime dataFim,
                                                       Pageable pageable);

    @Query("SELECT new com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViajeEmpresaDTOJPQ(v, s, d) " +
            "FROM ParadaModel s, ParadaModel d, ViagemModel v " +
            "WHERE s.empresa.id = :id_empresa AND s.lugar.id = :id_lugar " +
            "AND s.dataHora BETWEEN :data_start AND :data_end AND s.tipo != 'DESTINO' " +
            "AND s.viagem.id = d.viagem.id AND d.tipo = 'DESTINO' " +
            "AND v.id = s.viagem.id")
    List<ViajeEmpresaDTOJPQ> findByEmpresaAndStartInInterval(@Param("id_empresa") UUID idEmpresa,
                                                             @Param("id_lugar") Integer idLugar,
                                                             @Param("data_start") LocalDateTime dataStart,
                                                             @Param("data_end") LocalDateTime dataEnd);

    @Query("SELECT new com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViajeEmpresaDTOJPQ(v, s, d) " +
            "FROM ParadaModel s, ParadaModel d, ViagemModel v " +
            "WHERE s.empresa.id = :id_empresa AND s.lugar.id = :id_lugar_salida " +
            "AND s.dataHora BETWEEN :data_start AND :data_end AND s.tipo != 'DESTINO' " +
            "AND s.viagem.id = d.viagem.id AND d.lugar.id = :id_lugar_destino AND d.tipo != 'SALIDA' " +
            "AND v.id = s.viagem.id")
    List<ViajeEmpresaDTOJPQ> findByEmpresaAndStartInInterval(@Param("id_empresa") UUID idEmpresa,
                                                             @Param("id_lugar_salida") Integer idLugarSalida,
                                                             @Param("id_lugar_destino") Integer idLugarDestino,
                                                             @Param("data_start") LocalDateTime dataStart,
                                                             @Param("data_end") LocalDateTime dataEnd);

    @Query("SELECT new com.alvaro.empresas.passagens.dtos.viajes.JPQL.ViajeDTOJPQL(v, s, d) " +
            "FROM ViagemModel v, ParadaModel s, ParadaModel d " +
            "WHERE v.empresa.id = :empresaId " +
            "AND v.dataHoraSalida BETWEEN :dataInicio AND :dataFim " +
            "AND v.autobus.id = :autobusId " +
            "AND s.viagem.id = v.id AND s.tipo = 'SALIDA' " +
            "AND d.viagem.id = v.id AND d.tipo = 'DESTINO' ")
    Page<ViajeDTOJPQL> findByEmpresaAndAutobusAndStartInInterval(UUID empresaId, Integer autobusId, LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable);


    Optional<ViagemModel> findFirst1ByAutobusId(Integer idAutobus);

    @Query(value = "SELECT v.* FROM tb_viagem v, tb_parada d " +
            "WHERE v.fk_idtb_empresa = :empresaId " +
            "AND v.data_hora_salida BETWEEN :inicioAlterado AND :fim " +
            "AND v.fk_idtb_autobus = :autobusId " +
            "AND v.cancelado = false AND d.fk_idtb_viagem = v.idtb_viagem AND d.tipo = 'DESTINO' AND d.data_hora >= :inicio " +
            "LIMIT 2",
            nativeQuery = true)
    List<ViagemModel> findByAutobusInIntervalo(UUID empresaId, Integer autobusId, LocalDateTime inicio, LocalDateTime inicioAlterado, LocalDateTime fim);

    @Query("SELECT new com.alvaro.empresas.passagens.dtos.viajes.JPQL.ViajeDTOJPQLRelatorio(v, s.lugarId, d.lugarId) " +
            "FROM ViagemModel v, ParadaModel s, ParadaModel d " +
            "WHERE d.empresa.id = :empresaId " +
            "AND d.dataHora BETWEEN :dataInicio AND :dataFim " +
            "AND d.tipo = 'DESTINO' " +
            "AND v.id = d.viagem.id " +
            "AND s.viagem.id = v.id AND s.tipo = 'SALIDA'")
    List<ViajeDTOJPQLRelatorio> findByEmpresaFinishedInInterval(UUID empresaId, LocalDateTime dataInicio, LocalDateTime dataFim);

    @Query("SELECT new com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViajeBuscaDTOJPQL(s.viagem.id, s.viagem.empresa.logo, s, d) " +
            "FROM ParadaModel s, ParadaModel d " +
            "WHERE s.lugar.id = :id_lugar_salida " +
            "AND s.dataHora BETWEEN :data_start AND :data_end " +
            "AND s.tipo != 'DESTINO' " +
            "AND s.viagem.id = d.viagem.id " +
            "AND d.lugar.id = :id_lugar_destino " +
            "AND d.tipo != 'SALIDA'")
    List<ViajeBuscaDTOJPQL> findByStartInInterval(
            @Param("id_lugar_salida") Integer idLugarSalida,
            @Param("id_lugar_destino") Integer idLugarDestino,
            @Param("data_start") LocalDateTime dataStart,
            @Param("data_end") LocalDateTime dataEnd);


}