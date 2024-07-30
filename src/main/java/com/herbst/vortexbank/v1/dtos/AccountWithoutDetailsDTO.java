package com.herbst.vortexbank.v1.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AccountWithoutDetailsDTO implements Serializable {
    @JsonProperty("id")
    private Long accountId;
    private String name;
    private String CPF;
    private String email;
    private String walletKey;
}
