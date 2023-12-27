package com.alvaro.empresas.passagens.paradas.services;

import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParadaService {
    @Autowired
    private ParadaRepository paradaRepository;
}
