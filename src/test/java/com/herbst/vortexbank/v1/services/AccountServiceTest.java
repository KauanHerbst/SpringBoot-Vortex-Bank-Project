package com.herbst.vortexbank.v1.services;

import com.herbst.vortexbank.entities.Account;
import com.herbst.vortexbank.exceptions.AccountIsActiveException;
import com.herbst.vortexbank.exceptions.EntityNotFoundException;
import com.herbst.vortexbank.repositories.AccountRepository;
import com.herbst.vortexbank.v1.dtos.ChangeAccountPasswordDTO;
import com.herbst.vortexbank.v1.dtos.CreateAccountPasswordDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createAccountPassword_AccountExists(){

        CreateAccountPasswordDTO dto = new CreateAccountPasswordDTO();
        dto.setName("Test");
        dto.setCPF("9999991");
        dto.setEmail("test@gmail.com");
        dto.setPassword("12345");

        Account account = new Account();
        account.setId(1L);
        account.setName(dto.getName());
        account.setCPF(dto.getCPF());
        account.setEmail(dto.getEmail());
        account.setEnabled(false);

        when(accountRepository.existsByNameAndEmailAndCPF(dto.getName(), dto.getEmail(), dto.getCPF()))
                .thenReturn(true);
        when(accountRepository.findByCPF(dto.getCPF())).thenReturn(account);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodePassword");

        boolean result = accountService.createAccountPassword(dto);

        Assertions.assertTrue(result);
        verify(accountRepository).save(account);

    }

    @Test
    public void createAccountPassword_AccountDoesNotExists(){
        CreateAccountPasswordDTO dto = new CreateAccountPasswordDTO();
        dto.setName("Test");
        dto.setCPF("9999991");
        dto.setEmail("test@gmail.com");
        dto.setPassword("12345");

        when(accountRepository.existsByNameAndEmailAndCPF(dto.getName(), dto.getEmail(), dto.getCPF()))
                .thenReturn(false);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
           accountService.createAccountPassword(dto);
        });

        verify(accountRepository, times(1)).existsByNameAndEmailAndCPF(dto.getName(),
                dto.getEmail(), dto.getCPF());
    }

    @Test
    public void createAccountPassword_AccountIsActivity(){
        CreateAccountPasswordDTO dto = new CreateAccountPasswordDTO();
        dto.setName("Test");
        dto.setCPF("9999991");
        dto.setEmail("test@gmail.com");
        dto.setPassword("12345");

        Account account = new Account();
        account.setId(1L);
        account.setName(dto.getName());
        account.setCPF(dto.getCPF());
        account.setEmail(dto.getEmail());
        account.setEnabled(true);

        when(accountRepository.existsByNameAndEmailAndCPF(dto.getName(), dto.getEmail(), dto.getCPF()))
                .thenReturn(true);
        when(accountRepository.findByCPF(dto.getCPF())).thenReturn(account);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodePassword");
        Assertions.assertThrows(AccountIsActiveException.class, () -> {
            accountService.createAccountPassword(dto);
        });

        verify(accountRepository, times(1)).existsByNameAndEmailAndCPF(dto.getName(),
                dto.getEmail(), dto.getCPF());
        verify(accountRepository, times(1)).findByCPF(dto.getCPF());
    }

    @Test
    public void changeAccountPassword_AccountExists(){
        ChangeAccountPasswordDTO dto = new ChangeAccountPasswordDTO();
        dto.setName("Test");
        dto.setCPF("9999991");
        dto.setEmail("test@gmail.com");
        dto.setPassword("12345");
        dto.setNewPassword("654321");

        Account account = new Account();
        account.setId(1L);
        account.setName(dto.getName());
        account.setCPF(dto.getCPF());
        account.setEmail(dto.getEmail());
        account.setPassword(dto.getPassword());
        account.setEnabled(true);

        when(accountRepository.existsByNameAndEmailAndCPF(dto.getName(), dto.getEmail(), dto.getCPF()))
                .thenReturn(true);
        when(accountRepository.findByCPF(dto.getCPF())).thenReturn(account);
        when(passwordEncoder.matches(dto.getPassword(), account.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(dto.getNewPassword())).thenReturn("encodePassword");

        boolean result = accountService.changeAccountPassword(dto);
        Assertions.assertTrue(result);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void changeAccountPassword_AccountDoesNotExists(){
        ChangeAccountPasswordDTO dto = new ChangeAccountPasswordDTO();
        dto.setName("Test");
        dto.setCPF("9999991");
        dto.setEmail("test@gmail.com");
        dto.setPassword("12345");
        dto.setNewPassword("654321");
        when(accountRepository.existsByNameAndEmailAndCPF(dto.getName(), dto.getEmail(), dto.getCPF()))
                .thenReturn(false);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            accountService.changeAccountPassword(dto);
        });

        verify(accountRepository, times(1)).existsByNameAndEmailAndCPF(dto.getName(),
                dto.getEmail(), dto.getCPF());

    }

    @Test
    public void loadUserByUsername_UserExists(){
        String usernameTest = "Test";

        Account account = new Account();
        account.setId(1L);
        account.setName(usernameTest);
        account.setEnabled(true);

        when(accountRepository.findByName(usernameTest)).thenReturn(account);
        Account accountResult = (Account) accountService.loadUserByUsername(usernameTest);
        verify(accountRepository, times(1)).findByName(usernameTest);
        Assertions.assertEquals(account, accountResult);

    }

    @Test
    public void loadUserByUsername_UserDoesNotExists(){
        String usernameTest = "Test";

        Account account = new Account();
        account.setId(1L);
        account.setName("Test Test");
        account.setEnabled(true);

        when(accountRepository.findByName(usernameTest)).thenReturn(null);
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            accountService.loadUserByUsername(usernameTest);
        });
        verify(accountRepository, times(1)).findByName(usernameTest);

    }

    @Test
    public void loadUserByNameAndCPFAndEmail_UserExists(){
        String name = "Test";
        String email = "test@gmail.com";
        String CPF = "999991";

        Account account = new Account();
        account.setId(1L);
        account.setName(name);
        account.setEmail(email);
        account.setCPF(CPF);
        account.setEnabled(true);

        when(accountRepository.findByNameAndEmailAndCPF(name, email, CPF)).thenReturn(account);
        Account accountResult = (Account) accountService.loadUserByNameAndCPFAndEmail(name, CPF, email);
        verify(accountRepository, times(1)).findByNameAndEmailAndCPF(name, email, CPF);
        Assertions.assertEquals(account, accountResult);

    }

    @Test
    public void loadUserByNameAndCPFAndEmail_UserDoesNotExists(){
        String name = "Test";
        String email = "test@gmail.com";
        String CPF = "999991";

        Account account = new Account();
        account.setId(1L);
        account.setName("Test Test");
        account.setEmail(email);
        account.setCPF(CPF);
        account.setEnabled(true);

        when(accountRepository.findByNameAndEmailAndCPF(name, email, CPF)).thenReturn(null);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
          accountService.loadUserByNameAndCPFAndEmail(name, CPF, email);
        });
        verify(accountRepository, times(1)).findByNameAndEmailAndCPF(name, email, CPF);
    }
}
