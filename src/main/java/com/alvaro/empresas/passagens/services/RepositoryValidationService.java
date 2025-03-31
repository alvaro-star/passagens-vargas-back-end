package com.alvaro.empresas.passagens.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.repositories.PrecoRepository;

@Service
public class RepositoryValidationService {
    @Autowired
    private PrecoRepository precoRepository;

    public boolean viagemHasPassagem(ViagemModel viagem) {
        Integer nPasajes;
        for (PrecoModel precio : viagem.getPrecos()) {
            nPasajes = precoRepository.calcularNPassagens(precio.getId());
            if (nPasajes != null && nPasajes > 0)
                return true;
        }
        return false;
    }
}
