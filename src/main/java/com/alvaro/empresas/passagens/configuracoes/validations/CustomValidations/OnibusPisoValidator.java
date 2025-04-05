package com.alvaro.empresas.passagens.configuracoes.validations.CustomValidations;

import com.alvaro.empresas.passagens.onibus.dtos.PisoInputDTO;
import com.alvaro.empresas.passagens.onibus.enums.TipePosicao;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OnibusPisoValidator implements ConstraintValidator<OnibusPiso, PisoInputDTO> {
    @Override
    public boolean isValid(PisoInputDTO dto, ConstraintValidatorContext context) {
        boolean valido = true;

        context.disableDefaultConstraintViolation();

        String erroNLinhas = validNLinhas(dto.nLinhas());
        if (!erroNLinhas.isEmpty()) {
            context.buildConstraintViolationWithTemplate(erroNLinhas)
                    .addPropertyNode("nLinhas")
                    .addConstraintViolation();
            valido = false;
        }

        String erroNColunas = validNColunas(dto.nColunas());
        if (!erroNColunas.isEmpty()) {
            context.buildConstraintViolationWithTemplate(erroNColunas)
                    .addPropertyNode("nColunas")
                    .addConstraintViolation();
            valido = false;
        }

        String erroDistribuicao = validDistribuicaoFileira(dto.distribuicaoFileira());
        if (!erroDistribuicao.isEmpty()) {
            context.buildConstraintViolationWithTemplate(erroDistribuicao)
                    .addPropertyNode("distribuicaoFileira")
                    .addConstraintViolation();
            valido = false;
        }


        String erroPosicoesBloqueadas = "";
        if (dto.nLinhas() != null && dto.nColunas() != null)
            erroPosicoesBloqueadas = validarPosicoesBloquedas(dto.nPosicoes(), dto.posicoesBloquedas());
        if (!erroPosicoesBloqueadas.isEmpty()) {
            context.buildConstraintViolationWithTemplate(erroPosicoesBloqueadas)
                    .addPropertyNode("posicoesBloqueadas")
                    .addConstraintViolation();
            valido = false;
        }

        return valido;
    }


    private static String validNLinhas(Integer nLinhas) {
        if (nLinhas == null || nLinhas == 0)
            return "Não pode ser nulo";
        if (nLinhas < 1)
            return "O valor deve ser no mínimo 1";
        return "";
    }

    private static String validNColunas(Integer nColunas) {
        if (nColunas == null || nColunas == 0)
            return "Não pode ser nulo";
        if (nColunas > 4 || nColunas < 1)
            return "Deve estar entre 1 e 4";
        return "";
    }

    private static String validDistribuicaoFileira(TipePosicao distribuicaoFileira) {
        if (distribuicaoFileira == null)
            return "Não pode ser um valor nulo";
        return "";
    }

    private static String validarPosicoesBloquedas(Integer nPosicoes, List<Integer> posicoesBloquedas) {
        if (posicoesBloquedas == null) {
            return "";
        }
        Set<Integer> conjunto = new HashSet<>();
        for (Integer posicao : posicoesBloquedas) {
            if (posicao < 1)
                return "Um numero precisa ser positivo e não nulo";
            if (!conjunto.add(posicao))
                return "As posições devem ser únicas";
            if (posicao > nPosicoes)
                return "Uma posição não existe";
        }
        return "";
    }
}
