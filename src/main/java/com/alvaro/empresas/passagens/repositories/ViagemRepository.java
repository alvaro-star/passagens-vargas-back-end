package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.dtos.viagens.JPQL.ViagemDTOJPQL;
import com.alvaro.empresas.passagens.dtos.viagens.JPQL.ViagemWithLogoDTOJPQL;
import com.alvaro.empresas.passagens.interfaces.ICustomRepository;
import com.alvaro.empresas.passagens.models.ViagemModel;
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
public interface ViagemRepository extends JpaRepository<ViagemModel, UUID>, ICustomRepository<ViagemModel, UUID> {
    Page<ViagemModel> findByEmpresaId(UUID id, Pageable pageable);

    @Query("SELECT new com.alvaro.empresas.passagens.dtos.viagens.JPQL.ViagemWithLogoDTOJPQL(v, v.empresa.logo) FROM ViagemModel v WHERE v.id = :id")
    Optional<ViagemWithLogoDTOJPQL> findByIdWithLogo(UUID id);

    @Query("SELECT v FROM ViagemModel v WHERE v.empresa.id = :empresaId AND v.dataHoraSaida >= :dataHoraSaida")
    Page<ViagemModel> findAfterDate(UUID empresaId, LocalDateTime dataHoraSaida, Pageable pageable);


    @Query("SELECT new com.alvaro.empresas.passagens.dtos.viagens.JPQL.ViagemDTOJPQL(v, s, d) " +
            "FROM ViagemModel v, ParadaModel s, ParadaModel d " +
            "WHERE v.empresa.id = :empresaId " +
            "AND v.dataHoraSaida BETWEEN :dataInicio AND :dataFim " +
            "AND s.viagem.id = v.id AND s.tipo = 'SAIDA' " +
            "AND d.viagem.id = v.id AND d.tipo = 'DESTINO'")
    Page<ViagemDTOJPQL> findByEmpresaAndStartInInterval(UUID empresaId,
                                                        LocalDateTime dataInicio,
                                                        LocalDateTime dataFim,
                                                        Pageable pageable);

    @Query("SELECT new com.alvaro.empresas.passagens.dtos.viagens.JPQL.ViagemDTOJPQL(v, s, d) " +
            "FROM ParadaModel s, ParadaModel d, ViagemModel v " +
            "WHERE s.empresa.id = :id_empresa AND s.lugar.id = :id_lugar " +
            "AND s.dataHora BETWEEN :data_start AND :data_end AND s.tipo != 'DESTINO' " +
            "AND s.viagem.id = d.viagem.id AND d.tipo = 'DESTINO' " +
            "AND v.id = s.viagem.id")
    List<ViagemDTOJPQL> findByEmpresaAndStartInInterval(@Param("id_empresa") UUID idEmpresa,
                                                        @Param("id_lugar") Integer idLugar,
                                                        @Param("data_start") LocalDateTime dataStart,
                                                        @Param("data_end") LocalDateTime dataEnd);

    @Query("SELECT new com.alvaro.empresas.passagens.dtos.viagens.JPQL.ViagemDTOJPQL(v, s, d) " +
            "FROM ParadaModel s, ParadaModel d, ViagemModel v " +
            "WHERE s.empresa.id = :id_empresa AND s.lugar.id = :id_lugar_saida " +
            "AND s.dataHora BETWEEN :data_start AND :data_end AND s.tipo != 'DESTINO' " +
            "AND s.viagem.id = d.viagem.id AND d.lugar.id = :id_lugar_destino AND d.tipo != 'SAIDA' " +
            "AND v.id = s.viagem.id")
    List<ViagemDTOJPQL> findByEmpresaAndStartInInterval(@Param("id_empresa") UUID idEmpresa,
                                                        @Param("id_lugar_saida") Integer idLugarSaida,
                                                        @Param("id_lugar_destino") Integer idLugarDestino,
                                                        @Param("data_start") LocalDateTime dataStart,
                                                        @Param("data_end") LocalDateTime dataEnd);

    @Query("SELECT new com.alvaro.empresas.passagens.dtos.viagens.JPQL.ViagemDTOJPQL(v, s, d) " +
            "FROM ViagemModel v, ParadaModel s, ParadaModel d " +
            "WHERE v.empresa.id = :empresaId " +
            "AND v.dataHoraSaida BETWEEN :dataInicio AND :dataFim " +
            "AND v.onibus.id = :onibusId " +
            "AND s.viagem.id = v.id AND s.tipo = 'SAIDA' " +
            "AND d.viagem.id = v.id AND d.tipo = 'DESTINO' ")
    Page<ViagemDTOJPQL> findByEmpresaAndOnibusAndStartInInterval(UUID empresaId, UUID onibusId, LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable);


    Optional<ViagemModel> findFirst1ByOnibusId(UUID idOnibus);

    @Query(value = "SELECT v.* FROM tb_viagem v, tb_parada d " +
            "WHERE v.fk_idtb_empresa = :empresaId " +
            "AND v.data_hora_saida BETWEEN :inicioAlterado AND :fim " +
            "AND v.fk_idtb_onibus = :onibusId " +
            "AND v.cancelado = false AND d.fk_idtb_viagem = v.idtb_viagem AND d.tipo = 'DESTINO' AND d.data_hora >= :inicio " +
            "LIMIT 2",
            nativeQuery = true)
    List<ViagemModel> findByOnibusInIntervalo(UUID empresaId, UUID onibusId, LocalDateTime inicio, LocalDateTime inicioAlterado, LocalDateTime fim);

    @Query("SELECT v " +
            "FROM ViagemModel v, ParadaModel s, ParadaModel d " +
            "WHERE d.empresa.id = :empresaId " +
            "AND d.dataHora BETWEEN :dataInicio AND :dataFim " +
            "AND d.tipo = 'DESTINO' " +
            "AND v.id = d.viagem.id " +
            "AND s.viagem.id = v.id AND s.tipo = 'SAIDA'")
    List<ViagemModel> findByEmpresaFinishedInInterval(UUID empresaId, LocalDateTime dataInicio, LocalDateTime dataFim);

    @Query("SELECT new com.alvaro.empresas.passagens.dtos.viagens.JPQL.ViagemWithLogoDTOJPQL(s.viagem, s.viagem.empresa.logo, s, d) " +
            "FROM ParadaModel s, ParadaModel d " +
            "WHERE s.lugar.id = :id_lugar_saida " +
            "AND s.dataHora BETWEEN :data_start AND :data_end " +
            "AND s.tipo != 'DESTINO' " +
            "AND s.viagem.id = d.viagem.id " +
            "AND d.lugar.id = :id_lugar_destino " +
            "AND d.tipo != 'SAIDA'")
    List<ViagemWithLogoDTOJPQL> findByStartInInterval(
            @Param("id_lugar_saida") Integer idLugarSaida,
            @Param("id_lugar_destino") Integer idLugarDestino,
            @Param("data_start") LocalDateTime dataStart,
            @Param("data_end") LocalDateTime dataEnd);


}