package com.herbst.vortexbank.exceptions;

import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ValidationError extends StandardError{
    private List<FieldMessage> errors = new ArrayList<>();

    public ValidationError(String error, String message, String path, int status, Instant timestamp) {
        super(error, message, path, status, timestamp);
    }

    public void addError(String fieldName, String message){
        errors.add(new FieldMessage(fieldName, message));
    }
}
