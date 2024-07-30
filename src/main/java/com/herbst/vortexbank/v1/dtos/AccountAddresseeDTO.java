package com.herbst.vortexbank.v1.dtos;

import lombok.*;

import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AccountAddresseeDTO implements Serializable {
    private String keySearch;
}
