package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.repositories.AsientoBloqueadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsientoBloqueadosService {
    @Autowired
    private AsientoBloqueadoRepository asientoBloqueadoRepository;
}
