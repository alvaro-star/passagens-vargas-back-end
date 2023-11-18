package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.repositories.AutobusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AutobusService {
    @Autowired
    private AutobusRepository autobusRepository;
}
