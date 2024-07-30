package com.herbst.vortexbank.exceptions;

public class InvalidAccountException extends RuntimeException{

    public InvalidAccountException(String message){
        super(message);
    }

    public InvalidAccountException(){
        super("Invalid Account");
    }

}
