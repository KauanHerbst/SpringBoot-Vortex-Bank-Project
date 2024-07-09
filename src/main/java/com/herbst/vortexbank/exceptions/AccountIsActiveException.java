package com.herbst.vortexbank.exceptions;

public class AccountIsActiveException extends RuntimeException{
    public AccountIsActiveException(){
        super("Account is active");
    }
}
