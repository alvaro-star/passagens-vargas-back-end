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
        Integer nPassagens;
        for (PrecoModel preco : viagem.getPrecos()) {
            nPassagens = precoRepository.calcularNPassagens(preco.getId());
            if (nPassagens != null && nPassagens > 0)
                return true;
        }
        return false;
    }
}
