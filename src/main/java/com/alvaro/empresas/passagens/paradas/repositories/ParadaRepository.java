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
    @Query(value = "SELECT fk_idtb_trayecto FROM tb_parada " +
            "WHERE fk_idtb_lugar = :idLugar " +
            "AND dataHora BETWEEN :dataStart AND :dataEnd", nativeQuery = true)
    List<UUID> cargarSalidasDelDia(Integer idLugar, LocalDateTime dataStart, LocalDateTime dataEnd);

    //Muito cuidado para nao pedir uma sobrecarga, es uma funcao especifica
    @Query(value = "SELECT * FROM tb_parada WHERE fk_idtb_trayecto = :idtb_trayecto ORDER BY dataHora", nativeQuery = true)
    List<ParadaModel> cargarParadasdoTrayecto(@Param("idtb_trayecto") UUID codigo);
}
