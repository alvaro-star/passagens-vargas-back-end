package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.repositories.LayoutBusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LayoutBusService {
    @Autowired
    private LayoutBusRepository layoutBusRepository;
}
