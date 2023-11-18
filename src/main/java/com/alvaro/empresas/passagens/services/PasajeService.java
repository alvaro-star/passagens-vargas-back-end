package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasajeService {
    @Autowired
    private PasajeRepository pasajeRepository;
}
