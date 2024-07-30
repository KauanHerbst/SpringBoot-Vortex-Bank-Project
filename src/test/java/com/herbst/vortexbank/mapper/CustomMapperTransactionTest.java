package com.herbst.vortexbank.mapper;

import com.herbst.vortexbank.entities.Account;
import com.herbst.vortexbank.entities.Transaction;
import com.herbst.vortexbank.util.TransactionStatusEnum;
import com.herbst.vortexbank.util.TransactionTypeEnum;
import com.herbst.vortexbank.v1.dtos.TransactionDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class CustomMapperTransactionTest {

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void mapperObject_success(){
        Account accountOrigin = new Account();
        accountOrigin.setId(1L);
        accountOrigin.setName("Test 1");
        accountOrigin.setCPF("99999999991");

        Account accountAddressee = new Account();
        accountAddressee.setId(2L);
        accountAddressee.setName("Test 2");
        accountAddressee.setCPF("99999999992");

        Transaction transaction = new Transaction();
        transaction.setId("TRANSACTION_ID");
        transaction.setAmount(100D);
        transaction.setTransactionStatus(TransactionStatusEnum.COMPLETED);
        transaction.setTransactionType(TransactionTypeEnum.DEPOSIT);
        transaction.setDescription("Mapper Test");
        transaction.setAccountOrigin(accountOrigin);
        transaction.setAccountAddressee(accountAddressee);

        TransactionDTO dto = CustomMapperTransaction.mapperObject(transaction);

        Assertions.assertEquals(transaction.getId(), dto.getId());
        Assertions.assertEquals(transaction.getTransactionType(), dto.getTransactionType());
        Assertions.assertEquals(transaction.getTransactionStatus(), dto.getTransactionStatus());
        Assertions.assertEquals(transaction.getDescription(), dto.getDescription());
        Assertions.assertEquals(transaction.getAmount(), dto.getAmount());
        Assertions.assertEquals(transaction.getAccountOrigin().getName(), dto.getAccountOrigin().getName());
        Assertions.assertEquals(transaction.getAccountOrigin().getCPF(), dto.getAccountOrigin().getCPF());
        Assertions.assertEquals(transaction.getAccountAddressee().getName(), dto.getAccountAddressee().getName());
        Assertions.assertEquals(transaction.getAccountAddressee().getCPF(), dto.getAccountAddressee().getCPF());
    }

}