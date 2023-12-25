package com.alvaro.empresas.passagens.lugares.services;

import com.alvaro.empresas.passagens.lugares.repositories.LugarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LugarService {
    @Autowired
    private LugarRepository lugarRepository;
}
