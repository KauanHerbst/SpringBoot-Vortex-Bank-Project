package com.herbst.vortexbank.v1.dtos;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CreateAccountPasswordDTO implements Serializable {
    private String name;
    private String email;
    private String CPF;
    private String password;
}
