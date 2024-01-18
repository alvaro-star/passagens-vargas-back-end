package com.alvaro.empresas.passagens.autobuses.services;

import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.models.PosicionIndisponibleModel;
import com.alvaro.empresas.passagens.autobuses.repositories.PosicionIndisponibleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PosicionIndisponibleService {
    @Autowired
    private PosicionIndisponibleRepository posicionRepository;

    public List<Integer> saveAll(List<Integer> dtos, PisoModel piso) {
        List<Integer> sillas = new ArrayList<>();
        dtos.forEach(numero -> {
            var model = new PosicionIndisponibleModel(numero);
            model.setPiso(piso);
            var modelSaved = posicionRepository.save(model);
            sillas.add(modelSaved.getNumero());
        });

        return sillas;
    }

    public void deleteAll(List<PosicionIndisponibleModel> list) {
        List<Integer> ids = new ArrayList<>();
        list.forEach(model -> {
            ids.add(model.getId());
        });
        ids.forEach(id -> {
            var model = posicionRepository.findById(id).get();
            posicionRepository.delete(model);
        });
    }
}
