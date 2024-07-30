package com.herbst.vortexbank.exceptions;

public class TransactionFailedException extends RuntimeException{
    public TransactionFailedException(String message){
        super(message);
    }

    public TransactionFailedException(){
        super("Transaction Failed");
    }
}
