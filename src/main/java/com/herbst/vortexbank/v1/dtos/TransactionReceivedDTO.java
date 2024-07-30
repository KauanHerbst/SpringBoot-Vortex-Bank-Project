package com.herbst.vortexbank.v1.dtos;

import com.herbst.vortexbank.util.TransactionTypeEnum;
import com.herbst.vortexbank.util.ValidEnum;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TransactionReceivedDTO {
    private String name;
    private String cpf;
    private String walletPassword;
    private String WalletKey;
    private Double amount;
    private String description;
    private String accountAddresseeName;
    private String accountAddresseeCPF;
    @ValidEnum(enumClass = TransactionTypeEnum.class, message = "Invalid Transaction Type Value")
    private String transactionType;
    private String transactionRefundId;
    private String transactionPaymentId;
}
