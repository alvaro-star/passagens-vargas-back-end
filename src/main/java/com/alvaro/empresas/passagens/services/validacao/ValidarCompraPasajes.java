package com.alvaro.empresas.passagens.services.validacao;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationWithErrorListExceptions;
import com.alvaro.empresas.passagens.configuracoes.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.dtos.pasagens.PasagemDTO;
import com.alvaro.empresas.passagens.dtos.pasagens.PaagensDTO;
import com.alvaro.empresas.passagens.dtos.pasagens.PasagensDTOVenta;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ValidarCompraPasajes {
    public void validarPasajesDTOVenta(BindingResult bindingResult, PasagensDTOVenta pasajesDTO, String path) {
        //Validacion normal
        int i;
        FieldMessageItemList itemList;

        List<FieldMessageList> errorsList = new ArrayList<>();
        List<FieldMessageItemList> itensErrados = new ArrayList<>();
        List<FieldMessage> errors = new ArrayList<>();

        bindingResult.getFieldErrors().forEach(error -> errors.add(new FieldMessage(error)));

        for (i = 0; i < pasajesDTO.pasajes().size(); i++) {
            itemList = validarPasajeDto(i, pasajesDTO.pasajes().get(i));
            if (itemList != null) itensErrados.add(itemList);
        }
        if (!itensErrados.isEmpty())
            errorsList.add(new FieldMessageList("pasajes", itensErrados));

        if (!errorsList.isEmpty() || !errors.isEmpty())
            throw new ValidationWithErrorListExceptions("Erro de validacao", errors, errorsList);
    }


    public void validarPasajesDTO(BindingResult bindingResult, PaagensDTO paagensDTO, String path) {
        //Validacion normal
        int i;
        FieldMessageItemList itemList;
        List<FieldMessageList> errorsList = new ArrayList<>();
        List<FieldMessageItemList> itensErrados = new ArrayList<>();
        List<FieldMessage> errors = new ArrayList<>();

        bindingResult.getFieldErrors().forEach(error -> errors.add(new FieldMessage(error)));

        for (i = 0; i < paagensDTO.pasajes().size(); i++) {
            itemList = validarPasajeDto(i, paagensDTO.pasajes().get(i));
            if (itemList != null)
                itensErrados.add(itemList);
        }
        if (!itensErrados.isEmpty())
            errorsList.add(new FieldMessageList("pasajes", itensErrados));
        if (!errorsList.isEmpty() || !errors.isEmpty())
            throw new ValidationWithErrorListExceptions("Erro de validacao", errors, errorsList);
    }

    private static FieldMessageItemList validarPasajeDto(int indice, PasagemDTO pasagemDTO) {
        String message;
        FieldMessageItemList itemList = new FieldMessageItemList();

        itemList.setIndex(indice);
        message = validateCarnet(pasagemDTO.documento());
        if (!message.isEmpty())
            itemList.addError(new FieldMessage("carnet", message));
        message = validateNombre(pasagemDTO.nome(), 50);
        if (!message.isEmpty())
            itemList.addError(new FieldMessage("nombre", message));
        message = validateNascimento(pasagemDTO.nascimento());
        if (!message.isEmpty())
            itemList.addError(new FieldMessage("nascimento", message));
        message = validateNSilla(pasagemDTO.numeroAssento());
        if (!message.isEmpty())
            itemList.addError(new FieldMessage("nSilla", message));

        if (!itemList.getErrors().isEmpty())
            return itemList;
        itemList = null;
        return itemList;
    }

    private static String validateCarnet(String carnet) {
        if (carnet == null || carnet.isBlank())
            return "El carnet no puede estar en blanco";
        if (carnet.length() < 7 || carnet.length() > 8)
            return "El carnet debe tener exactamente 7 o 8 caracteres";
        if (!saoTodosDigitos(carnet))
            return "El carnet debe tener solo caracteres numericos";
        return "";
    }

    public static boolean saoTodosDigitos(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i)))
                return false;
        }
        return true;
    }

    private static String validateNombre(String nombre, int size) {
        if (nombre == null || nombre.isBlank())
            return "El nombre no puede estar en blanco";
        if (nombre.length() > size)
            return "El nombre no puede tener más de 50 caracteres";
        return "";
    }

    private static String validateNascimento(Date nascimento) {
        if (nascimento == null)
            return "La fecha de nacimiento no puede ser nula";
        if (nascimento.after(new Date()))
            return "La fecha de nacimiento debe ser una fecha pasada";
        return "";
    }

    private static String validateNSilla(Integer nSilla) {
        if (nSilla == null)
            return "El número de silla no puede ser nulo";
        return "";
    }
}
