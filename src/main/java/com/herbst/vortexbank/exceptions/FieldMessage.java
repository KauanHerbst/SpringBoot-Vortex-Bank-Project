package com.herbst.vortexbank.exceptions;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FieldMessage {
    private String fieldName;
    private String message;
}
