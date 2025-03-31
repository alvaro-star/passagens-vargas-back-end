package com.alvaro.empresas.passagens.onibus.services.validacao;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationWithErrorListExceptions;
import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusCreateDTO;
import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoCreateDTO;
import com.alvaro.empresas.passagens.onibus.enums.TipePosicao;
import com.alvaro.empresas.passagens.onibus.repositories.OnibusRepository;
import com.alvaro.empresas.passagens.services.validacao.FieldMessageItemList;
import com.alvaro.empresas.passagens.services.validacao.FieldMessageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ValidarPiso {
    public void validarOnibusDTO(OnibusCreateDTO dto) {
        int i;
        FieldMessageItemList itemList;

        List<FieldMessageList> listaErros = new ArrayList<>();
        List<FieldMessageItemList> itensErrados = new ArrayList<>();

        for (i = 0; i < dto.pisos().size(); i++) {
            itemList = validarPisoDTO(i, dto.pisos().get(i));
            if (itemList != null) itensErrados.add(itemList);
        }

        if (!itensErrados.isEmpty())
            listaErros.add(new FieldMessageList("pisos", itensErrados));
        if (!listaErros.isEmpty())
            throw new ValidationWithErrorListExceptions("Erro de validação", new HashMap<>(), listaErros);
    }

    private static FieldMessageItemList validarPisoDTO(int indice, PisoCreateDTO dto) {
        String mensagem;
        FieldMessageItemList itemList = new FieldMessageItemList();
        itemList.setIndex(indice);

        mensagem = validarNLinhas(dto.nLinhas());
        if (!mensagem.isEmpty()) {
            itemList.addError("nLinhas", mensagem);
        }

        mensagem = validarNColunas(dto.nColunas());
        if (!mensagem.isEmpty()) {
            itemList.addError("nColunas", mensagem);
        }

        mensagem = validarDistribuicaoFileira(dto.distribuicaoFileira());
        if (!mensagem.isEmpty())
            itemList.addError("distribuicaoFileira", mensagem);

        mensagem = validarPosicoesBloquedas(dto.nLinhas() * dto.nColunas(), dto.posicoesBloquedas());
        if (!mensagem.isEmpty())
            itemList.addError("posicoesBloquedas", mensagem);

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

    private static String validarPosicoesBloquedas(Integer nPosicoes, List<Integer> posicoesBloquedas) {
        Set<Integer> conjunto = new HashSet<>();
        for (Integer posicao : posicoesBloquedas) {
            if (posicao < 1)
                return "Uma posição não pode ser nula ou negativa";
            if (!conjunto.add(posicao))
                return "Uma posição aparece repetida";
            if (posicao > nPosicoes)
                return "Uma posição não existe";
        }

        return "";
    }
}