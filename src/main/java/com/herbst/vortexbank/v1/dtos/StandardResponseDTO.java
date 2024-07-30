package com.herbst.vortexbank.v1.dtos;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StandardResponseDTO implements Serializable {
    private String message;
    private int status;
    private Instant timestamp;

    public StandardResponseDTO(String message, int status){
        this.timestamp = Instant.now();
        this.message = message;
        this.status = status;
    }

}
