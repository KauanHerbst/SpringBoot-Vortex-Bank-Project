package com.herbst.vortexbank.exceptions;

public class ArgumentIsEmptyException extends RuntimeException{
    public ArgumentIsEmptyException(String argument){
        super("Argument " + argument + " is Empty");
    }
}
