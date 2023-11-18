package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.repositories.ParadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParadaService {
    @Autowired
    private ParadaRepository paradaRepository;
}
