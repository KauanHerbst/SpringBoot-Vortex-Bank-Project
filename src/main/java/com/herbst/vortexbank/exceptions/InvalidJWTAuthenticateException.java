package com.herbst.vortexbank.exceptions;

public class InvalidJWTAuthenticateException extends RuntimeException{
    public InvalidJWTAuthenticateException(String msg){
        super(msg);
    }
}
