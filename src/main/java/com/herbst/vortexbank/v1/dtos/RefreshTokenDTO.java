package com.herbst.vortexbank.v1.dtos;

import lombok.*;

import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class RefreshTokenDTO implements Serializable {
    private String CPF;
    private String refreshToken;
}
