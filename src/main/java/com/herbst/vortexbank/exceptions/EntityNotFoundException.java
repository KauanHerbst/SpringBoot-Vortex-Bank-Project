package com.herbst.vortexbank.exceptions;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(){
        super("Entity Not Found");
    }
}
