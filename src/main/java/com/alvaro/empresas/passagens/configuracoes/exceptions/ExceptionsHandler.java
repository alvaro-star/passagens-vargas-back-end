package com.alvaro.empresas.passagens.configuracoes.exceptions;

import java.sql.SQLIntegrityConstraintViolationException;

import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.EntityNotFoundException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationWithErrorListExceptions;
import com.alvaro.empresas.passagens.configuracoes.exceptions.dtos.EntityNotFoundError;
import com.alvaro.empresas.passagens.configuracoes.exceptions.dtos.StandardError;
import com.alvaro.empresas.passagens.configuracoes.exceptions.dtos.ValidationError;
import com.alvaro.empresas.passagens.services.validacao.ValidationErrorsWithList;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ExceptionsHandler {
    private static final String defaultMessage = "Contacte-se con el supervisor";

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public StandardError exception(Exception ex, HttpServletRequest request) {
        log.error(ex.getMessage());

        return new StandardError(
                System.currentTimeMillis(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                defaultMessage,
                request.getRequestURI());
    }

    @ExceptionHandler(RestRuntimeException.class)
    public ResponseEntity<StandardError> restRuntimeException(RestRuntimeException ex, HttpServletRequest request) {
        StandardError error = new StandardError(
                System.currentTimeMillis(),
                ex.getStatus(),
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public EntityNotFoundError entityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        return new EntityNotFoundError(
                System.currentTimeMillis(),
                HttpStatus.NOT_FOUND,
                request.getRequestURI(),
                ex.getMessage(),
                ex.getId(),
                EntityNotFoundException.clearEntityName(ex.getEntityClass())
        );
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public StandardError objectNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return new StandardError(
                System.currentTimeMillis(),
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public StandardError validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        ValidationError err = new ValidationError(
                System.currentTimeMillis(),
                HttpStatus.UNPROCESSABLE_ENTITY,
                e.getMessage(),
                request.getRequestURI());
        e.getBindingResult().getFieldErrors().forEach(err::addError);
        return err;
    }

    @ExceptionHandler(ValidationWithErrorListExceptions.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ValidationErrorsWithList validationWithErrorListExceptions(
            ValidationWithErrorListExceptions ex, HttpServletRequest request) {
        ValidationErrorsWithList err = new ValidationErrorsWithList(
                System.currentTimeMillis(),
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Erro durante a validacao",
                null);
        err.setErrorsList(ex.getErrorsList());
        return err;
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public StandardError nullPointerException(NullPointerException ex, HttpServletRequest request) {
        log.error(ex.getMessage());
        return new StandardError(
                System.currentTimeMillis(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                defaultMessage,
                request.getRequestURI());
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public StandardError erroAssociacao(SQLIntegrityConstraintViolationException e,
                                        HttpServletRequest request) {
        log.error(e.getMessage());
        return new StandardError(
                System.currentTimeMillis(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                defaultMessage,
                request.getRequestURI());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ValidationError erroValidacao(ValidationException e, HttpServletRequest request) {
        return new ValidationError(
                System.currentTimeMillis(),
                HttpStatus.UNPROCESSABLE_ENTITY,
                e.getMessage(),
                request.getRequestURI(),
                e.getErrors());
    }

    @ExceptionHandler(NoSuchMethodError.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public StandardError exception(NoSuchMethodError e, HttpServletRequest request) {
        log.error(e.getMessage());
        return new StandardError(
                System.currentTimeMillis(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage(),
                request.getRequestURI());
    }
}
