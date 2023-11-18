package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.LayoutBusModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LayoutBusRepository extends JpaRepository<LayoutBusModel, Integer> {
}
