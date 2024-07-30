package com.herbst.vortexbank.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationError> validationErrorException(MethodArgumentNotValidException exception, HttpServletRequest request){
        String error = "Erro de Validação";
        HttpStatus status = HttpStatus.NOT_FOUND;
        ValidationError err = new ValidationError(error, "Error de validação nos campos",
                request.getRequestURI(), status.value(), Instant.now());

        exception.getBindingResult().getFieldErrors().forEach(fieldError ->
            err.addError(fieldError.getField(), fieldError.getDefaultMessage())
        );

        return ResponseEntity.status(status).body(err);
    }

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

    @ExceptionHandler(AccountIsNotActiveException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<StandardError> accountIsNotActiveException(AccountIsNotActiveException exception, HttpServletRequest request){
        String error = "Conta não ativada";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(error, exception.getMessage(), request.getRequestURI(), status.value(),
                Instant.now());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(InvalidAccountException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<StandardError> invalidAccountException(InvalidAccountException exception, HttpServletRequest request){
        String error = "Conta Inválida";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(error, exception.getMessage(), request.getRequestURI(), status.value(),
                Instant.now());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<StandardError> invalidTransactionException(InvalidTransactionException exception, HttpServletRequest request){
        String error = "Transação Inválida";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(error, exception.getMessage(), request.getRequestURI(), status.value(),
                Instant.now());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(InvalidWalletKeyException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<StandardError> invalidWalletKeyException(InvalidWalletKeyException exception, HttpServletRequest request){
        String error = "Chave da carteira Inválida";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(error, exception.getMessage(), request.getRequestURI(), status.value(),
                Instant.now());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(TransactionFailedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<StandardError> transactionFailedException(TransactionFailedException exception, HttpServletRequest request){
        String error = "Falha na Transação";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(error, exception.getMessage(), request.getRequestURI(), status.value(),
                Instant.now());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(WalletIsActiveException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<StandardError> walletIsActiveException(WalletIsActiveException exception, HttpServletRequest request){
        String error = "Carteira já ativada";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(error, exception.getMessage(), request.getRequestURI(), status.value(),
                Instant.now());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(WalletIsNotActiveException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<StandardError> walletIsNotActiveException(WalletIsNotActiveException exception, HttpServletRequest request){
        String error = "Carteira não ativada";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(error, exception.getMessage(), request.getRequestURI(), status.value(),
                Instant.now());
        return ResponseEntity.status(status).body(err);
    }
}
