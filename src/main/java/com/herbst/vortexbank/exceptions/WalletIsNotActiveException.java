package com.herbst.vortexbank.exceptions;

public class WalletIsNotActiveException extends RuntimeException{
    public WalletIsNotActiveException(){
        super("Wallet is not active");
    }
}
