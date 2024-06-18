package com.alvaro.empresas.passagens.services.validacao;

import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajeDTO;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTO;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTOVenta;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ValidarCompraPasajes {
    public static ValidationErrorsWithList validarPasajesDTOVenta(BindingResult bindingResult, PasajesDTOVenta pasajesDTO, String path) {
        //Validacion normal
        int i;
        FieldMessageItemList itemList;
        List<FieldMessageItemList> itensErrados = new ArrayList<>();
        ValidationErrorsWithList err = new ValidationErrorsWithList(System.currentTimeMillis(), HttpStatus.UNPROCESSABLE_ENTITY.value(), "Erro de Validacao", "Erro durante a validacao", path);
        for (FieldError erro : bindingResult.getFieldErrors())
            err.addError(erro.getField(), erro.getDefaultMessage());

        for (i = 0; i < pasajesDTO.pasajes().size(); i++) {
            itemList = validarPasajeDto(i, pasajesDTO.pasajes().get(i));
            if (itemList != null)
                itensErrados.add(itemList);
        }
        if (!itensErrados.isEmpty())
            err.addErrorList(new FieldMessageList("pasajes", itensErrados));
        return err;
    }
    public static ValidationErrorsWithList validarPasajesDTO(BindingResult bindingResult, PasajesDTO pasajesDTO, String path) {
        //Validacion normal
        int i;
        FieldMessageItemList itemList;
        List<FieldMessageItemList> itensErrados = new ArrayList<>();
        ValidationErrorsWithList err = new ValidationErrorsWithList(System.currentTimeMillis(), HttpStatus.UNPROCESSABLE_ENTITY.value(), "Erro de Validacao", "Erro durante a validacao", path);
        for (FieldError erro : bindingResult.getFieldErrors())
            err.addError(erro.getField(), erro.getDefaultMessage());

        for (i = 0; i < pasajesDTO.pasajes().size(); i++) {
            itemList = validarPasajeDto(i, pasajesDTO.pasajes().get(i));
            if (itemList != null)
                itensErrados.add(itemList);
        }
        if (!itensErrados.isEmpty())
            err.addErrorList(new FieldMessageList("pasajes", itensErrados));
        return err;
    }

    private static FieldMessageItemList validarPasajeDto(int indice, PasajeDTO pasajeDTO) {
        String message;
        FieldMessageItemList itemList = new FieldMessageItemList();

        itemList.setIndex(indice);
        message = validateCarnet(pasajeDTO.carnet());
        if (!message.isEmpty())
            itemList.addError(new FieldMessage("carnet", message));
        message = validateNombre(pasajeDTO.nombre(), 50);
        if (!message.isEmpty())
            itemList.addError(new FieldMessage("nombre", message));
        message = validateNascimento(pasajeDTO.nascimento());
        if (!message.isEmpty())
            itemList.addError(new FieldMessage("nascimento", message));
        message = validateNSilla(pasajeDTO.nSilla());
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
        if (carnet.length() != 7)
            return "El carnet debe tener exactamente 7 caracteres";
        return "";
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
