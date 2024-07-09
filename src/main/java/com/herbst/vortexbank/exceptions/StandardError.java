package com.herbst.vortexbank.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StandardError {

    private String error;

    private String message;

    private String path;

    private int status;

    private Instant timestamp;

}