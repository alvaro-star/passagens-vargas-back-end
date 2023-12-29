package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.dtos.PrecioDTO;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.repositories.PrecioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PrecioService {
    @Autowired
    private PrecioRepository precioRepository;

    public List<PrecioDTO> saveAll(List<PrecioModel> dtoModels) {
        List<PrecioDTO> salvos = new ArrayList<>();
        for (PrecioModel precioModel : dtoModels) {
            var save = precioRepository.save(precioModel);
            salvos.add(new PrecioDTO(save));
        }
        return salvos;
    }
}
