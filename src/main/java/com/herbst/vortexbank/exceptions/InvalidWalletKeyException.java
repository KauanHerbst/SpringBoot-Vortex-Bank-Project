package com.herbst.vortexbank.exceptions;

public class InvalidWalletKeyException extends RuntimeException{
    public InvalidWalletKeyException(){
        super("Invalid Wallet Key");
    }
}
