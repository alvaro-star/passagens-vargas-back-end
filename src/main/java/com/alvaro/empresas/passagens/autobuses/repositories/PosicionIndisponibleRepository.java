package com.alvaro.empresas.passagens.autobuses.repositories;

import com.alvaro.empresas.passagens.autobuses.models.PosicionIndisponibleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosicionIndisponibleRepository extends JpaRepository<PosicionIndisponibleModel, Long> {
}
