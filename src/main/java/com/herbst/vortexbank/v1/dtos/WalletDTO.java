package com.herbst.vortexbank.v1.dtos;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class WalletDTO implements Serializable {
    private Long walletId;
    private Double balance;
    private String password;
    private Boolean enabled;
    private String typeOfCurrency;
    private Boolean walletNonLocked;
    private Date walletCreatedAt;
}
