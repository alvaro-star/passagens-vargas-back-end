package com.alvaro.empresas.passagens.repositories;

import com.alvaro.empresas.passagens.models.ContactoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactoRepository extends JpaRepository<ContactoModel, Long> {
}
