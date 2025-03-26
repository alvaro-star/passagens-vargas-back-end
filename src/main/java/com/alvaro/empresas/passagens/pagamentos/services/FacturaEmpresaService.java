package com.alvaro.empresas.passagens.pagamentos.services;

import com.alvaro.empresas.passagens.pagamentos.models.FaturaEmpresaModel;
import com.alvaro.empresas.passagens.pagamentos.repositories.FacturaEmpresaRepository;
import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FacturaEmpresaService {
    @Autowired
    private FacturaEmpresaRepository fERepository;
    @Autowired
    private PasajeRepository pasajeRepository;

    public FaturaEmpresaModel findById(UUID id) {
        var model = fERepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, FaturaEmpresaModel.class.getName()));
    }
}
