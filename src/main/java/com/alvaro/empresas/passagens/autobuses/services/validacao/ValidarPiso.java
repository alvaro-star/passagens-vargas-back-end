package com.alvaro.empresas.passagens.autobuses.services.validacao;

import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOCreate;
import com.alvaro.empresas.passagens.autobuses.enums.TypePosicao;
import com.alvaro.empresas.passagens.autobuses.repositories.AutobusRepository;
import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.configurations.exceptions.InternalException.ValidationWithErrorListExceptions;
import com.alvaro.empresas.passagens.services.validacao.FieldMessageItemList;
import com.alvaro.empresas.passagens.services.validacao.FieldMessageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ValidarPiso {
    @Autowired
    private AutobusRepository autobusRepository;

    public void validarAutobusDTO(BindingResult bindingResult, AutobusDTO dto) {
        int i;
        FieldMessageItemList itemList;
        List<FieldMessageList> errorsList = new ArrayList<>();
        List<FieldMessageItemList> itensErrados = new ArrayList<>();
        List<FieldMessage> errors = new ArrayList<>();
        bindingResult.getFieldErrors().forEach(error -> errors.add(new FieldMessage(error)));

        if (!bindingResult.hasFieldErrors("placa"))
            if (autobusRepository.existsByPlaca(dto.placa()))
                errors.add(new FieldMessage("placa", "La placa ya esta registrada"));

        for (i = 0; i < dto.pisos().size(); i++) {
            itemList = validarPisoDTO(i, dto.pisos().get(i));
            if (itemList != null) itensErrados.add(itemList);
        }
        if (!itensErrados.isEmpty())
            errorsList.add(new FieldMessageList("pisos", itensErrados));
        if (!errorsList.isEmpty() || !errors.isEmpty())
            throw new ValidationWithErrorListExceptions("Erro de validacao", errors, errorsList);
    }

    private static FieldMessageItemList validarPisoDTO(int indice, PisoDTOCreate dto) {
        String message;
        FieldMessageItemList itemList = new FieldMessageItemList();
        itemList.setIndex(indice);

        message = validarNLinhas(dto.getNLinhas());
        if (!message.isEmpty()) {
            itemList.addError(new FieldMessage("nLinhas", message));
        }

        message = validarNColunas(dto.getNColunas());
        if (!message.isEmpty()) {
            itemList.addError(new FieldMessage("nColunas", message));
        }

        message = validarDistribuicaoFileira(dto.getDistribuicaoFileira());
        if (!message.isEmpty())
            itemList.addError(new FieldMessage("distribuicaoFileira", message));

        message = validarPosicoesBloqueadas(dto.getPosicionesBloqueadas());
        if (!message.isEmpty())
            itemList.addError(new FieldMessage("posicionesBloqueadas", message));

        if (!itemList.getErrors().isEmpty())
            return itemList;
        return null;
    }

    private static String validarNLinhas(Integer nLinhas) {
        if (nLinhas == null || nLinhas == 0)
            return "No puede ser nulo";
        return "";
    }

    private static String validarNColunas(Integer nColunas) {
        if (nColunas == null || nColunas == 0)
            return "No puede ser nulo";
        if (nColunas > 4)
            return "No puede ser maior que 4";
        return "";
    }

    private static String validarDistribuicaoFileira(TypePosicao distribuicaoFileira) {
        if (distribuicaoFileira == null)
            return "No puede ser un valor nulo";
        return "";
    }

    private static String validarPosicoesBloqueadas(List<Integer> posicoesBloqueadas) {
        Set<Integer> set = new HashSet<>();
        for (Integer posicion : posicoesBloqueadas) {
            if (posicion < 1)
                return "Una posicion no puede ser nula o negativa";
            if (!set.add(posicion))
                return "Una posicion aparece repetida"; // Se não conseguiu adicionar ao conjunto, é porque já existe
        }

        return "";
    }
}
