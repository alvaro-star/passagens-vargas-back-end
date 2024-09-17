package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.pagos.models.FacturaEmpresaModel;
import com.alvaro.empresas.passagens.repositories.FacturaEmpresaRepository;
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

    public FacturaEmpresaModel findById(UUID id) {
        var model = fERepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, FacturaEmpresaModel.class.getName()));
    }

}
