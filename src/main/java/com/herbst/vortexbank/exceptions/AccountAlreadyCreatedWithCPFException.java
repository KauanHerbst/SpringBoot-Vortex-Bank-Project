package com.herbst.vortexbank.exceptions;

public class AccountAlreadyCreatedWithCPFException extends RuntimeException{
    public AccountAlreadyCreatedWithCPFException(){
        super("Account Already Created With CPF Used");
    }
}
