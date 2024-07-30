package com.herbst.vortexbank.mapper;

import com.herbst.vortexbank.entities.Transaction;
import com.herbst.vortexbank.v1.dtos.AccountTransactionDTO;
import com.herbst.vortexbank.v1.dtos.TransactionDTO;

public class CustomMapperTransaction {

    public static TransactionDTO mapperObject(Transaction transaction){
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setTransactionStatus(transaction.getTransactionStatus());
        dto.setAmount(transaction.getAmount());
        dto.setDescription(transaction.getDescription());
        AccountTransactionDTO accountOriginDTO = new AccountTransactionDTO(transaction.getAccountOrigin().getId(),
                transaction.getAccountOrigin().getName(), transaction.getAccountOrigin().getCPF());

        AccountTransactionDTO accountAddresseeDTO = new AccountTransactionDTO(transaction.getAccountAddressee().getId(),
                transaction.getAccountAddressee().getName(), transaction.getAccountAddressee().getCPF());

        dto.setAccountOrigin(accountOriginDTO);
        dto.setAccountAddressee(accountAddresseeDTO);
        dto.setTransactionCreatedAt(transaction.getTransactionCreatedAt());
        if(transaction.getTransactionRefund() != null){
            dto.setTransactionRefundId(transaction.getTransactionRefund().getId());
        }
        return dto;
    }
}
