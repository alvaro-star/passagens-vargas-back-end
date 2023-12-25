package com.alvaro.empresas.passagens.autobuses.repositories;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutobusRepository extends JpaRepository<AutobusModel, Integer> {
}
