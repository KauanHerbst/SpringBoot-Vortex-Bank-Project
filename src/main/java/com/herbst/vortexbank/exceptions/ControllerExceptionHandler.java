package com.herbst.vortexbank.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<StandardError> entityNotFoundException(EntityNotFoundException exception, HttpServletRequest request){
        String error = "Entidade não encontrada";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(error, exception.getMessage(), request.getRequestURI(), status.value(),
                Instant.now());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(AccountAlreadyCreatedWithCPFException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<StandardError> accountAlreadyCreatedWithCPFException(AccountAlreadyCreatedWithCPFException exception, HttpServletRequest request){
        String error = "Conta já criada com CPF enviado";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(error, exception.getMessage(), request.getRequestURI(), status.value(),
                Instant.now());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(AccountAlreadyCreatedWithEmailException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<StandardError> accountAlreadyCreatedWithEmailException(AccountAlreadyCreatedWithEmailException exception, HttpServletRequest request){
        String error = "Conta já criada com Email enviado";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(error, exception.getMessage(), request.getRequestURI(), status.value(),
                Instant.now());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(AccountIsActiveException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<StandardError> accountIsActiveException(AccountIsActiveException exception, HttpServletRequest request){
        String error = "Conta já Ativa";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(error, exception.getMessage(), request.getRequestURI(), status.value(),
                Instant.now());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(InvalidJWTAuthenticateException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<StandardError> invalidJWTAuthenticateException(InvalidJWTAuthenticateException exception, HttpServletRequest request){
        String error = "Autenticação Inválida";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(error, exception.getMessage(), request.getRequestURI(), status.value(),
                Instant.now());
        return ResponseEntity.status(status).body(err);
    }

}
