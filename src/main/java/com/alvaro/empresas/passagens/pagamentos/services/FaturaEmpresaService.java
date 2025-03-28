package com.alvaro.empresas.passagens.pagamentos.services;

import com.alvaro.empresas.passagens.pagamentos.models.FaturaEmpresaModel;
import com.alvaro.empresas.passagens.pagamentos.repositories.FaturaEmpresaRepository;
import com.alvaro.empresas.passagens.repositories.PassagemRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FaturaEmpresaService {
    @Autowired
    private FaturaEmpresaRepository fERepository;
    @Autowired
    private PassagemRepository passagemRepository;

    public FaturaEmpresaModel findById(UUID id) {
        var model = fERepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, FaturaEmpresaModel.class.getName()));
    }
}
