package com.alvaro.empresas.passagens.helpers.validations.validacao;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationWithErrorListExceptions;
import com.alvaro.empresas.passagens.dtos.pasagens.PassagemDTO;
import com.alvaro.empresas.passagens.dtos.pasagens.PassagensDTO;
import com.alvaro.empresas.passagens.dtos.pasagens.PassagensDTOVenta;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ValidarCompraPassagens {
    public void validarPassagensDTOVenta(PassagensDTOVenta passagensDTO, String path) {
        // Validação normal
        int i;
        FieldMessageItemList itemList;

        List<FieldMessageList> listaErros = new ArrayList<>();
        List<FieldMessageItemList> itensErrados = new ArrayList<>();

        for (i = 0; i < passagensDTO.passagens().size(); i++) {
            itemList = validarPassagemDTO(i, passagensDTO.passagens().get(i));
            if (itemList != null) itensErrados.add(itemList);
        }
        if (!itensErrados.isEmpty())
            listaErros.add(new FieldMessageList("passagens", itensErrados));

        if (!listaErros.isEmpty())
            throw new ValidationWithErrorListExceptions("Erro de validação", new HashMap<>(), listaErros);
    }

    public void validarPassagensDTO(BindingResult bindingResult, PassagensDTO passagensDTO, String path) {
        // Validação normal
        int i;
        FieldMessageItemList itemList;
        List<FieldMessageList> listaErros = new ArrayList<>();
        List<FieldMessageItemList> itensErrados = new ArrayList<>();

        var errors = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, e -> (e.getDefaultMessage() == null) ? "" : e.getDefaultMessage()));

        for (i = 0; i < passagensDTO.passagens().size(); i++) {
            itemList = validarPassagemDTO(i, passagensDTO.passagens().get(i));
            if (itemList != null)
                itensErrados.add(itemList);
        }
        if (!itensErrados.isEmpty())
            listaErros.add(new FieldMessageList("passagens", itensErrados));
        if (!listaErros.isEmpty() || !errors.isEmpty())
            throw new ValidationWithErrorListExceptions("Erro de validação", (HashMap<String, String>) errors, listaErros);
    }

    private static FieldMessageItemList validarPassagemDTO(int indice, PassagemDTO passagemDTO) {
        String mensagem;
        FieldMessageItemList itemList = new FieldMessageItemList();

        itemList.setIndex(indice);
        mensagem = validarDocumento(passagemDTO.documento());
        if (!mensagem.isEmpty())
            itemList.addError("documento", mensagem);
        mensagem = validarNome(passagemDTO.nome(), 50);
        if (!mensagem.isEmpty())
            itemList.addError("nome", mensagem);
        mensagem = validarNascimento(passagemDTO.nascimento());
        if (!mensagem.isEmpty())
            itemList.addError("nascimento", mensagem);
        mensagem = validarNAssento(passagemDTO.nAssento());
        if (!mensagem.isEmpty())
            itemList.addError("nAssento", mensagem);

        if (!itemList.getErrors().isEmpty())
            return itemList;
        itemList = null;
        return itemList;
    }

    private static String validarDocumento(String carnet) {
        if (carnet == null || carnet.isBlank())
            return "O carnet não pode estar em branco";
        if (carnet.length() < 7 || carnet.length() > 8)
            return "O carnet deve ter exatamente 7 ou 8 caracteres";
        if (!saoTodosDigitos(carnet))
            return "O carnet deve ter apenas caracteres numéricos";
        return "";
    }

    public static boolean saoTodosDigitos(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i)))
                return false;
        }
        return true;
    }

    private static String validarNome(String nome, int tamanhoMaximo) {
        if (nome == null || nome.isBlank())
            return "O nome não pode estar em branco";
        if (nome.length() > tamanhoMaximo)
            return "O nome não pode ter mais de 50 caracteres";
        return "";
    }

    private static String validarNascimento(Date nascimento) {
        if (nascimento == null)
            return "A data de nascimento não pode ser nula";
        if (nascimento.after(new Date()))
            return "A data de nascimento deve ser uma data passada";
        return "";
    }

    private static String validarNAssento(Integer nSilla) {
        if (nSilla == null)
            return "O número de cadeira não pode ser nulo";
        return "";
    }
}
