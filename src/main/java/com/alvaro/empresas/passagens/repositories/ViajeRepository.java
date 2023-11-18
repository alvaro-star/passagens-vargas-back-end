package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.ViajeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViajeRepository extends JpaRepository<ViajeModel, Integer> {
}
