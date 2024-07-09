package com.herbst.vortexbank.mapper;

import com.herbst.vortexbank.entities.Account;
import com.herbst.vortexbank.v1.dtos.AccountDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MapperObjectTest {

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void mapper_ObjectValueTest(){
        Account account = new Account();
        account.setEnabled(true);
        account.setName("Test");
        account.setPassword("123");
        account.setId(1l);
        account.setCPF("99999991");
        account.setEmail("test@gmail.com");
        account.setAccountNonLocked(true);
        account.setAccountNonExpired(true);
        account.setCredentialsNonExpired(true);

        AccountDTO accountDTO = MapperObject.objectValue(account, AccountDTO.class);

        Assertions.assertNotNull(accountDTO);
        Assertions.assertTrue(accountDTO.getEnabled());
        Assertions.assertTrue(accountDTO.getAccountNonExpired());
        Assertions.assertTrue(accountDTO.getAccountNonLocked());
        Assertions.assertTrue(accountDTO.getCredentialsNonExpired());
        Assertions.assertEquals(1L, accountDTO.getAccountId());
        Assertions.assertEquals("99999991", accountDTO.getCPF());
        Assertions.assertEquals("test@gmail.com", accountDTO.getEmail());
        Assertions.assertEquals("Test", accountDTO.getName());
    }

    @Test
    public void mapper_ObjectsValuesTest(){
        List<Account> accounts = new ArrayList<>();
        Account accountOne = new Account();
        accountOne.setId(1L);
        accountOne.setName("Test 1");
        accountOne.setEmail("test1@gmail.com");
        accountOne.setEnabled(true);
        accountOne.setAccountNonLocked(true);
        accountOne.setAccountNonExpired(true);
        accountOne.setCredentialsNonExpired(true);

        Account accountTwo = new Account();
        accountTwo.setId(2L);
        accountTwo.setName("Test 2");
        accountTwo.setEmail("test2@gmail.com");
        accountTwo.setEnabled(true);
        accountTwo.setAccountNonLocked(true);
        accountTwo.setAccountNonExpired(true);
        accountTwo.setCredentialsNonExpired(true);

        accounts.add(accountOne);
        accounts.add(accountTwo);

        List<AccountDTO> accountDTOS = MapperObject.objectsValues(accounts, AccountDTO.class);

        AccountDTO accountDTO_One = accountDTOS.get(0);
        AccountDTO accountDTO_Two = accountDTOS.get(1);

        Assertions.assertNotNull(accountDTOS);

        Assertions.assertEquals(accountOne.getName(), accountDTO_One.getName());
        Assertions.assertEquals(accountOne.getId(), accountDTO_One.getAccountId());
        Assertions.assertEquals(accountOne.getEmail(), accountDTO_One.getEmail());
        Assertions.assertTrue(accountDTO_One.getCredentialsNonExpired());
        Assertions.assertTrue(accountDTO_One.getAccountNonLocked());
        Assertions.assertTrue(accountDTO_One.getAccountNonExpired());
        Assertions.assertTrue(accountDTO_One.getEnabled());

        Assertions.assertEquals(accountTwo.getName(), accountDTO_Two.getName());
        Assertions.assertEquals(accountTwo.getId(), accountDTO_Two.getAccountId());
        Assertions.assertEquals(accountTwo.getEmail(), accountDTO_Two.getEmail());
        Assertions.assertTrue(accountDTO_Two.getCredentialsNonExpired());
        Assertions.assertTrue(accountDTO_Two.getAccountNonLocked());
        Assertions.assertTrue(accountDTO_Two.getAccountNonExpired());
        Assertions.assertTrue(accountDTO_Two.getEnabled());

    }
}