package com.herbst.vortexbank.v1.dtos;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CreateWalletPasswordDTO implements Serializable {
    private String name;
    private String CPF;
    private String walletKey;
    private String accountPassword;
    private String walletPassword;
}
