package com.alvaro.empresas.passagens.autobuses.repositories;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutobusRepository extends JpaRepository<AutobusModel, Integer> {
    boolean existsByPlaca(String placa);
    Page<AutobusModel> findByEmpresa(EmpresaModel empresaModel, Pageable pageable);
}
