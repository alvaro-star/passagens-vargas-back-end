package com.alvaro.empresas.passagens.pagamentos.services;

import java.util.UUID;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alvaro.empresas.passagens.pagamentos.models.FaturaEmpresaModel;
import com.alvaro.empresas.passagens.pagamentos.repositories.FaturaEmpresaRepository;

@Service
public class FaturaEmpresaService {
    @Autowired
    private FaturaEmpresaRepository fERepository;

    public FaturaEmpresaModel findById(UUID id) {
        var model = fERepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, FaturaEmpresaModel.class.getName()));
    }
}
