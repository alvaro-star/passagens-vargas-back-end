package com.alvaro.empresas.passagens.autobuses.services.validacao;

import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTO;
import com.alvaro.empresas.passagens.autobuses.enums.EnumPosicao;
import com.alvaro.empresas.passagens.autobuses.repositories.AutobusRepository;
import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.services.validacao.FieldMessageItemList;
import com.alvaro.empresas.passagens.services.validacao.FieldMessageList;
import com.alvaro.empresas.passagens.services.validacao.ValidationErrorsWithList;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ValidarPiso {
    public static ValidationErrorsWithList validarAutobusDTO(BindingResult bindingResult, AutobusDTO dto, AutobusRepository autobusRepository) {
        int i;
        FieldMessageItemList itemList;
        List<FieldMessageItemList> itensErrados = new ArrayList<>();
        ValidationErrorsWithList err = new ValidationErrorsWithList(
                System.currentTimeMillis(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Erro de Validacao",
                "Erro durante a validacao",
                "/autobuses");
        for (FieldError erro : bindingResult.getFieldErrors()) {
            err.addError(erro.getField(), erro.getDefaultMessage());
        }

        if (!bindingResult.hasFieldErrors("placa"))
            if (autobusRepository.existsByPlaca(dto.placa()))
                err.addError("placa", "La placa ya esta registrada");

        for (i = 0; i < dto.pisos().size(); i++) {
            itemList = validarPisoDTO(i, dto.pisos().get(i));
            if (itemList != null)
                itensErrados.add(itemList);
        }
        if (!itensErrados.isEmpty())
            err.addErrorList(new FieldMessageList("pisos", itensErrados));
        return err;
    }

    private static FieldMessageItemList validarPisoDTO(int indice, PisoDTO dto) {
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

    private static String validarDistribuicaoFileira(EnumPosicao distribuicaoFileira) {
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
