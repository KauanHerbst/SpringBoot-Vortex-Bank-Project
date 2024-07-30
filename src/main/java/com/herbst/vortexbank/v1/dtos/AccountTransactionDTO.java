package com.herbst.vortexbank.v1.dtos;

import lombok.*;

import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AccountTransactionDTO implements Serializable {
    private Long accountId;
    private String name;
    private String CPF;
}
