package com.herbst.vortexbank.v1.services;

import com.herbst.vortexbank.entities.Account;
import com.herbst.vortexbank.entities.Permission;
import com.herbst.vortexbank.exceptions.AccountAlreadyCreatedWithCPFException;
import com.herbst.vortexbank.exceptions.AccountAlreadyCreatedWithEmailException;
import com.herbst.vortexbank.exceptions.EntityNotFoundException;
import com.herbst.vortexbank.repositories.AccountRepository;
import com.herbst.vortexbank.repositories.PermissionReporitory;
import com.herbst.vortexbank.security.jwt.TokenProvider;
import com.herbst.vortexbank.v1.dtos.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private PermissionReporitory permissionReporitory;

    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createAccount_Success(){
        CreateAccountDTO dto = new CreateAccountDTO();
        dto.setName("Test");
        dto.setEmail("test@gmail.com");
        dto.setCPF("999991");
        dto.setTelephone("10101010");
        dto.setDateOfBirth("03/06/2004");

        Permission permissionAccount = new Permission();
        permissionAccount.setId(1L);
        permissionAccount.setName("ACCOUNT");

        when(accountRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(accountRepository.existsByCPF(dto.getCPF())).thenReturn(false);
        when(permissionReporitory.findById(1L)).thenReturn(Optional.of(permissionAccount));

        AccountDTO accountResultDTO = authService.create(dto);

        Assertions.assertEquals(dto.getName(), accountResultDTO.getName());
        Assertions.assertEquals(dto.getCPF(), accountResultDTO.getCPF());
        Assertions.assertEquals(dto.getEmail(), accountResultDTO.getEmail());
        Assertions.assertEquals(dto.getTelephone(), accountResultDTO.getTelephone());
        Assertions.assertEquals(dto.getDateOfBirth(), accountResultDTO.getDateOfBirth());
        Assertions.assertEquals(true, accountResultDTO.getCredentialsNonExpired());
        Assertions.assertEquals(true, accountResultDTO.getAccountNonLocked());
        Assertions.assertEquals(true, accountResultDTO.getAccountNonExpired());
        Assertions.assertEquals(false, accountResultDTO.getEnabled());

        verify(accountRepository, times(1)).save(any());
    }

    @Test
    public void createAccount_ExistsAccountWithEmail(){
        CreateAccountDTO dto = new CreateAccountDTO();
        dto.setName("Test");
        dto.setEmail("test@gmail.com");
        dto.setCPF("999991");
        dto.setTelephone("10101010");
        dto.setDateOfBirth("03/06/2004");

        when(accountRepository.existsByEmail(dto.getEmail())).thenReturn(true);
        Assertions.assertThrows(AccountAlreadyCreatedWithEmailException.class, () -> {
            authService.create(dto);
        });
    }

    @Test
    public void createAccount_ExistsAccountWithCPF(){
        CreateAccountDTO dto = new CreateAccountDTO();
        dto.setName("Test");
        dto.setEmail("test@gmail.com");
        dto.setCPF("999991");
        dto.setTelephone("10101010");
        dto.setDateOfBirth("03/06/2004");

        when(accountRepository.existsByCPF(dto.getCPF())).thenReturn(true);
        Assertions.assertThrows(AccountAlreadyCreatedWithCPFException.class, () -> {
            authService.create(dto);
        });
    }

    @Test
    public void signInAccount_Success(){
        AccountSignInDTO dto = new AccountSignInDTO();
        dto.setCPF("99999991");
        dto.setPassword("123456");

        Permission permissionAccount = new Permission();
        permissionAccount.setId(1L);
        permissionAccount.setName("ACCOUNT");

        Account account = new Account();
        account.setName("Test");
        account.setEmail("test@gmail.com");
        account.setCPF("99999991");
        account.setTelephone("10101010");
        account.setPassword("123456");
        account.setDateOfBirth("03/06/2004");
        account.setAccountNonExpired(true);
        account.setAccountNonLocked(true);
        account.setCredentialsNonExpired(true);
        account.setEnabled(true);
        account.getPermissions().add(permissionAccount);

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setName(account.getName());
        tokenDTO.setAccessToken("accessToken");
        tokenDTO.setRefreshToken("refreshToken");
        tokenDTO.setAuthenticated(true);

        when(accountRepository.existsByCPF(dto.getCPF())).thenReturn(true);
        when(accountRepository.findByCPF(dto.getCPF())).thenReturn(account);
        when(passwordEncoder.matches(account.getPassword(), dto.getPassword())).thenReturn(true);
        when(tokenProvider.createAccessToken(account.getName(), account.getCPF(), account.getEmail(),
                account.getPermissionsAccount())).thenReturn(tokenDTO);

        TokenDTO tokenResultDTO = authService.signin(dto);

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider, times(1)).createAccessToken(account.getName(), account.getCPF(),
                account.getEmail(), account.getPermissionsAccount());

        Assertions.assertEquals(account.getName(), tokenResultDTO.getName());
        Assertions.assertEquals("accessToken", tokenResultDTO.getAccessToken());
        Assertions.assertEquals("refreshToken", tokenResultDTO.getRefreshToken());
        Assertions.assertEquals(true, tokenResultDTO.getAuthenticated());

    }

    @Test
    public void signInAccount_DoesNotAccountExists(){
        AccountSignInDTO dto = new AccountSignInDTO();
        dto.setCPF("99999991");
        dto.setPassword("123456");

        when(accountRepository.existsByCPF(dto.getCPF())).thenReturn(false);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
           TokenDTO token = authService.signin(dto);
        });
        verify(accountRepository, times(1)).existsByCPF(dto.getCPF());
    }

    @Test
    public void signInAccount_BadCredentials(){
        AccountSignInDTO dto = new AccountSignInDTO();
        dto.setCPF("99999991");
        dto.setPassword("other password");

        Account account = new Account();
        account.setName("Test");
        account.setCPF("99999991");
        account.setPassword("password");


        when(accountRepository.existsByCPF(dto.getCPF())).thenReturn(true);
        when(accountRepository.findByCPF(dto.getCPF())).thenReturn(account);
        when(passwordEncoder.matches(account.getPassword(), dto.getPassword())).thenReturn(false);

        Assertions.assertThrows(BadCredentialsException.class, () -> {
            TokenDTO token = authService.signin(dto);
        });
        verify(authenticationManager, times(0)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void refreshToken_Success(){
        RefreshTokenDTO dto = new RefreshTokenDTO();
        dto.setCPF("999991");
        dto.setRefreshToken("refreshToken");

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setName("Test");
        tokenDTO.setAccessToken("accessToken");
        tokenDTO.setRefreshToken("refreshToken");
        tokenDTO.setAuthenticated(true);

        when(accountRepository.existsByCPF(dto.getCPF())).thenReturn(true);
        when(tokenProvider.refreshToken(dto.getRefreshToken())).thenReturn(tokenDTO);

        TokenDTO tokenResultDTO = authService.refreshToken(dto);

        Assertions.assertEquals("accessToken", tokenResultDTO.getAccessToken());
        Assertions.assertEquals("refreshToken", tokenResultDTO.getRefreshToken());

    }

    @Test public void refreshToken_DoesNotAccountExists(){
        RefreshTokenDTO dto = new RefreshTokenDTO();
        dto.setCPF("999991");
        dto.setRefreshToken("refreshToken");

        when(accountRepository.existsByCPF(dto.getCPF())).thenReturn(false);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            TokenDTO tokenResultDTO = authService.refreshToken(dto);
        });
        verify(tokenProvider, times(0)).refreshToken(dto.getRefreshToken());
    }
}