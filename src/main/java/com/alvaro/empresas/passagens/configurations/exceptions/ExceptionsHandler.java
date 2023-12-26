package com.alvaro.empresas.passagens.configurations.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class ExceptionsHandler {
    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<StandardError> objectNotFound(ObjectNotFoundException ex, HttpServletRequest request) {
        StandardError error = new StandardError(
                System.currentTimeMillis(),
                HttpStatus.NOT_FOUND.value(),
                "Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        ValidationError err = new ValidationError(
                System.currentTimeMillis(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Erro de Validacao",
                e.getMessage(),
                request.getRequestURI());
        for (FieldError erro : e.getBindingResult().getFieldErrors()) {
            err.addError(erro.getField(), erro.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(err);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Object> erroAssociacao(SQLIntegrityConstraintViolationException e, HttpServletRequest request) {
        StandardError err = new StandardError(
                System.currentTimeMillis(),
                HttpStatus.CONFLICT.value(),
                "A classe tem associados",
                e.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(err);
    }
}
