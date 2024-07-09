package com.herbst.vortexbank.v1.dtos;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AccountSignInDTO implements Serializable {
    private String CPF;
    private String password;
}
