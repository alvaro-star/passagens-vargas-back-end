package com.alvaro.empresas.passagens.onibus.services.validacao;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationWithErrorListExceptions;
import com.alvaro.empresas.passagens.configuracoes.exceptions.dtos.FieldMessage;
import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusDTO;
import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoDTOCreate;
import com.alvaro.empresas.passagens.onibus.enums.TipePosicao;
import com.alvaro.empresas.passagens.onibus.repositories.OnibusRepository;
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
    private OnibusRepository onibusRepository;

    public void validarOnibusDTO(BindingResult bindingResult, OnibusDTO dto) {
        int i;
        FieldMessageItemList itemList;

        List<FieldMessageList> listaErros = new ArrayList<>();
        List<FieldMessageItemList> itensErrados = new ArrayList<>();
        List<FieldMessage> erros = new ArrayList<>();

        bindingResult.getFieldErrors().forEach(error -> erros.add(new FieldMessage(error)));

        if (!bindingResult.hasFieldErrors("placa"))
            if (onibusRepository.existsByPlaca(dto.placa()))
                erros.add(new FieldMessage("placa", "A placa já está registrada"));

        for (i = 0; i < dto.pisos().size(); i++) {
            itemList = validarPisoDTO(i, dto.pisos().get(i));
            if (itemList != null) itensErrados.add(itemList);
        }
        if (!itensErrados.isEmpty())
            listaErros.add(new FieldMessageList("pisos", itensErrados));
        if (!listaErros.isEmpty() || !erros.isEmpty())
            throw new ValidationWithErrorListExceptions("Erro de validação", erros, listaErros);
    }

    private static FieldMessageItemList validarPisoDTO(int indice, PisoDTOCreate dto) {
        String mensagem;
        FieldMessageItemList itemList = new FieldMessageItemList();
        itemList.setIndex(indice);

        mensagem = validarNLinhas(dto.getNLinhas());
        if (!mensagem.isEmpty()) {
            itemList.addError(new FieldMessage("nLinhas", mensagem));
        }

        mensagem = validarNColunas(dto.getNColunas());
        if (!mensagem.isEmpty()) {
            itemList.addError(new FieldMessage("nColunas", mensagem));
        }

        mensagem = validarDistribuicaoFileira(dto.getDistribuicaoFileira());
        if (!mensagem.isEmpty())
            itemList.addError(new FieldMessage("distribuicaoFileira", mensagem));

        mensagem = validarPosicoesBloquedas(dto.getPosicoesBloquedas());
        if (!mensagem.isEmpty())
            itemList.addError(new FieldMessage("posicoesBloquedas", mensagem));

        if (!itemList.getErrors().isEmpty())
            return itemList;
        return null;
    }

    private static String validarNLinhas(Integer nLinhas) {
        if (nLinhas == null || nLinhas == 0)
            return "Não pode ser nulo";
        return "";
    }

    private static String validarNColunas(Integer nColunas) {
        if (nColunas == null || nColunas == 0)
            return "Não pode ser nulo";
        if (nColunas > 4)
            return "Não pode ser maior que 4";
        return "";
    }

    private static String validarDistribuicaoFileira(TipePosicao distribuicaoFileira) {
        if (distribuicaoFileira == null)
            return "Não pode ser um valor nulo";
        return "";
    }

    private static String validarPosicoesBloquedas(List<Integer> posicoesBloquedas) {
        Set<Integer> conjunto = new HashSet<>();
        for (Integer posicao : posicoesBloquedas) {
            if (posicao < 1)
                return "Uma posição não pode ser nula ou negativa";
            if (!conjunto.add(posicao))
                return "Uma posição aparece repetida"; // Se não conseguiu adicionar ao conjunto, é porque já existe
        }

        return "";
    }
}