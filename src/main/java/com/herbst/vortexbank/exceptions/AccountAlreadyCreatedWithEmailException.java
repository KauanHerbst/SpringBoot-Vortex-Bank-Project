package com.herbst.vortexbank.exceptions;

public class AccountAlreadyCreatedWithEmailException extends RuntimeException{
    public AccountAlreadyCreatedWithEmailException(){
        super("Account Already Created With Email Used");
    }
}
