package com.herbst.vortexbank.v1.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.herbst.vortexbank.entities.Account;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDTO implements Serializable {
    private String id;
    private String transactionType;
    private String transactionStatus;
    private Date transactionCreatedAt;
    private String transactionRefundId;
    private String description;
    private Double amount;
    private AccountTransactionDTO accountAddressee;
    private AccountTransactionDTO accountOrigin;
    private Boolean isPaymentDone;
}
