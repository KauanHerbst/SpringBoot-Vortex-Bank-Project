package com.herbst.vortexbank.exceptions;

public class InvalidTransactionException extends RuntimeException{

    public InvalidTransactionException(String message){
        super(message);
    }

    public InvalidTransactionException(){
        super("Invalid Transaction");
    }
}
