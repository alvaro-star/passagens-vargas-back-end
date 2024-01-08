package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.repositories.AutobusRepository;
import com.alvaro.empresas.passagens.autobuses.repositories.PisoRepository;
import com.alvaro.empresas.passagens.autobuses.repositories.PosicionIndisponibleRepository;
import com.alvaro.empresas.passagens.paradas.repositories.CiudadRepository;
import com.alvaro.empresas.passagens.paradas.repositories.DepartamentoRepository;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestDB {
    @Autowired
    private DepartamentoRepository departamentoRepository;
    @Autowired
    private CiudadRepository ciudadRepository;
    @Autowired
    private LugarRepository lugarRepository;
    @Autowired
    private ParadaRepository paradaRepository;
    @Autowired
    private AutobusRepository autobusRepository;
    @Autowired
    private PisoRepository pisoRepository;
    @Autowired
    private PosicionIndisponibleRepository posicionIndisponibleRepository;
    @Autowired
    private TrayectoRepository trayectoRepository;
    @Autowired
    private ViajeRepository viajeRepository;
    @Autowired
    private PasajeRepository pasajeRepository;
    @Autowired
    private PrecioRepository precioRepository;
    @Autowired
    private SillaRepository sillaRepository;
}
