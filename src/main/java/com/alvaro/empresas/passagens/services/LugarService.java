package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.repositories.LugarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LugarService {
    @Autowired
    private LugarRepository lugarRepository;
}
