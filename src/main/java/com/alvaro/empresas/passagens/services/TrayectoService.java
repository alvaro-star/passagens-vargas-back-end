package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.models.TrayectoModel;
import com.alvaro.empresas.passagens.repositories.TrayectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrayectoService {
    @Autowired
    private TrayectoRepository trayectoRepository;

}
