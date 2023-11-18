package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ViajeService {
    @Autowired
    private ViajeRepository viajeRepository;
}
