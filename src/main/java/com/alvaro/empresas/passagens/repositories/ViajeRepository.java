package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.ViajeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ViajeRepository extends JpaRepository<ViajeModel, UUID> {

    //viajeDestino.isAfter(viajeModelSalida) &&
    //viajeModelDestino.isAfter(viajeSalida)


    //Dado a alguns viajes, Si se pone el inervalo de tiempo al reves igual te devolvera el mismo registro

    //Tranquilo
    @Query("SELECT v.autobus.empresa.logo FROM ViajeModel v WHERE v.codigo = :codigo")
    String getLogoEmpresaFromViaje(UUID codigo);
}

/* No usage
@Query(value = "SELECT v.* FROM tb_viaje as v, tb_parada as s, tb_parada as d " +
            "WHERE v.fk_idtb_trayecto = :codigo " +
            "AND :startViaje < :endViaje " +
            "AND v.id_salida = s.idtb_parada " +
            "AND :startViaje >= s.data_hora " +
            "AND v.id_destino = d.idtb_parada " +
            "AND :endViaje <= d.data_hora", nativeQuery = true)
    List<ViajeModel> getFromTrayecto123(UUID codigo, LocalDateTime startViaje, LocalDateTime endViaje);
        @Query("SELECT vm FROM ViajeModel vm, ParadaModel S, ParadaModel D " +
            "WHERE vm.trayecto.codigo = :trayectoCodigo " +
            "AND vm.salida.id = S.id " +
            "AND :destino > S.dataHora " +
            "AND vm.destino.id = D.id " +
            "AND D.dataHora > :salida")
    List<ViajeModel> cargarViajesConIntervalosComunes(@Param("trayectoCodigo") UUID trayectoCodigo,
                                                      @Param("salida") LocalDateTime salida,
                                                      @Param("destino") LocalDateTime destino);

    @Query("SELECT vm FROM ViajeModel vm, ParadaModel S " +
            "WHERE S.lugar.id = :salidaId " +
            "AND S.dataHora >= :fechaHoraPartida " +
            "AND S.dataHora < :fechaPartida " +
            "AND vm.salida.id = S.id")
    List<ViajeModel> getViajesDeSalida(@Param("salidaId") Integer idSalida,
                                       @Param("fechaHoraPartida") LocalDateTime fechaHoraSalida,
                                       @Param("fechaPartida") LocalDateTime fechaPartida);
* //Steve
    @Query(value = "SELECT e.logo FROM tb_empresa as e, tb_autobus as a, tb_trayecto as t, tb_viaje as v " +
            "WHERE v.idtb_viaje = :id" +
            "AND t.idtb_trayecto = v.fk_idtb_trayecto " +
            "AND t.fk_idtb_autobus = a.idtb_autobus " +
            "AND a.fk_idtb_empresa = e.idtb_empresa", nativeQuery = true)
    String getLogoEmpresaFromViaje(Integer id);

    @Query(value = "SELECT count(v.idtb_viaje) FROM tb_viaje as v, tb_parada as s, tb_parada as d " +
            "WHERE v.fk_idtb_trayecto = :codigo " +
            "AND v.id_salida = s.idtb_parada " +
            "AND s.fk_idtb_lugar = :idParadaSalida " +
            "AND v.id_destino = d.idtb_parada " +
            "AND d.fk_idtb_lugar = :idParadaDestino ", nativeQuery = true)
    Integer getViajesIguais(UUID codigo, Integer idParadaSalida, Integer idParadaDestino);*/
