package com.alvaro.empresas.passagens.helpers.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IntegerListStringUtil {
    public static String convertListToString(String delimiter, List<Integer> numbers) {
        return numbers.stream().map(String::valueOf).collect(Collectors.joining(delimiter));
    }

    public static List<Integer> convertStringToIntegerList(String listString) {
        if (listString.isBlank())
            return new ArrayList<>();
        ArrayList<Integer> integerList = new ArrayList<>();
        String[] numeros = listString.split(",");

        try {
            for (String numero : numeros)
                integerList.add(Integer.parseInt(numero));
        } catch (NumberFormatException e) {
            log.error(listString, e.getMessage());
            throw new RestRuntimeException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Os dados de um dos pisos sao inconsistentes");
        }
        return integerList;
    }
}
