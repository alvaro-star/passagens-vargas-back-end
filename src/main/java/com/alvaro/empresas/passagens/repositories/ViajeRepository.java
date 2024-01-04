package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.ViajeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ViajeRepository extends JpaRepository<ViajeModel, Integer> {

    //viajeDestino.isAfter(viajeModelSalida) &&
    //viajeModelDestino.isAfter(viajeSalida)
    @Query("""
            SELECT vm FROM ViajeModel vm, ParadaModel S, ParadaModel D 
            WHERE vm.trayecto.codigo = :trayectoCodigo 
            AND vm.salida.id = S.id 
            AND :destino > S.dataHora 
            AND vm.destino.id = D.id 
            AND D.dataHora > :salida
            """)
    List<ViajeModel> cargarViajesConIntervalosComunes(@Param("trayectoCodigo") UUID trayectoCodigo,
                                                      @Param("salida") LocalDateTime salida,
                                                      @Param("destino") LocalDateTime destino);

    @Query("""
            SELECT vm FROM ViajeModel vm, ParadaModel S 
            WHERE S.lugar.id = :salidaId 
            AND S.dataHora >= :fechaHoraPartida 
            AND S.dataHora < :fechaPartida 
            AND vm.salida.id = S.id
            """)
    List<ViajeModel> getViajesDeSalida(@Param("salidaId") Integer idSalida,
                                       @Param("fechaHoraPartida") LocalDateTime fechaHoraSalida,
                                       @Param("fechaPartida") LocalDateTime fechaPartida);
}
