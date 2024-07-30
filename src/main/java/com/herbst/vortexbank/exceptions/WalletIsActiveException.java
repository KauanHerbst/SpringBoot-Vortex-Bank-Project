package com.herbst.vortexbank.exceptions;

public class WalletIsActiveException extends RuntimeException{
    public WalletIsActiveException(){
        super("Wallet is Active");
    }
}
