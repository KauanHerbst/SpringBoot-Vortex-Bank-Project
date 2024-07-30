package com.herbst.vortexbank.exceptions;

public class AccountIsNotActiveException extends RuntimeException{
    public AccountIsNotActiveException(){
        super("Account is Not Active");
    }
}
