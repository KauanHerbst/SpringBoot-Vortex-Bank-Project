package com.herbst.vortexbank.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.herbst.vortexbank.entities.Account;
import com.herbst.vortexbank.repositories.AccountRepository;
import com.herbst.vortexbank.v1.dtos.TokenDTO;
import com.herbst.vortexbank.v1.services.AccountService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TokenProviderTest {
    @Value("${security.jwt.token.secret_key:secret}")
    private String secretKey;
    @Value("${security.jwt.token.expire-length:3600000}")
    private Long validityInMilliSeconds;

    Algorithm algorithm = null;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TokenProvider tokenProvider;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        tokenProvider.init();
    }

    @Test
    public void getAccessToken_Success(){
        String name = "Test";
        String CPF = "9999991";
        String email = "test@gmail.com";
        Long id = 1L;
        List<String> permissions = new ArrayList<>();
        permissions.add("ACCOUNT");
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliSeconds);

        String accessToken = tokenProvider.getAccessToken(name, id, CPF, email, permissions, now, validity);
        System.out.println(accessToken);

        Assertions.assertNotNull(accessToken);
    }

    @Test
    public void getRefreshToken_Success(){
        String name = "Test";
        String CPF = "9999991";
        String email = "test@gmail.com";
        Long id = 1L;
        List<String> permissions = new ArrayList<>();
        permissions.add("ACCOUNT");
        Date now = new Date();

        String refreshToken = tokenProvider.getRefreshToken(name, id, CPF, email, permissions, now);
        System.out.println(refreshToken);

        Assertions.assertNotNull(refreshToken);
    }

    @Test
    public void createAccessToken_Success(){
        String name = "Test";
        String CPF = "9999991";
        String email = "test@gmail.com";
        Long id = 1L;
        List<String> permissions = new ArrayList<>();
        permissions.add("ACCOUNT");

        TokenDTO tokenDTO = tokenProvider.createAccessToken(name, id, CPF, email, permissions);

        Assertions.assertNotNull(tokenDTO);
        Assertions.assertEquals(name, tokenDTO.getName());
        Assertions.assertNotNull(tokenDTO.getAccessToken());
        Assertions.assertNotNull(tokenDTO.getRefreshToken());
        Assertions.assertNotNull(tokenDTO.getCreated());
        Assertions.assertTrue(tokenDTO.getAuthenticated());
    }

    @Test
    public void refreshToken_Success(){
        String name = "Test";
        String CPF = "9999991";
        String email = "test@gmail.com";
        Long id = 1L;
        List<String> permissions = new ArrayList<>();
        permissions.add("ACCOUNT");
        Date now = new Date();

        String tokenTest = tokenProvider.getRefreshToken(name, id, CPF, email, permissions, now);

        TokenDTO tokenDTO = tokenProvider.refreshToken(tokenTest);

        Assertions.assertNotNull(tokenDTO);
        Assertions.assertEquals(name, tokenDTO.getName());
        Assertions.assertNotNull(tokenDTO.getAccessToken());
        Assertions.assertNotNull(tokenDTO.getRefreshToken());
        Assertions.assertNotNull(tokenDTO.getCreated());
        Assertions.assertTrue(tokenDTO.getAuthenticated());
    }

    @Test
    public void validateToken_Success(){
        String name = "Test";
        String CPF = "9999991";
        String email = "test@gmail.com";
        Long id = 1L;
        List<String> permissions = new ArrayList<>();
        permissions.add("ACCOUNT");
        Date now = new Date();

        String tokenTest = tokenProvider.getRefreshToken(name, id, CPF, email, permissions, now);

        Boolean result = tokenProvider.validateToken(tokenTest);
        Assertions.assertTrue(result);
    }

    @Test
    public void validateToken_InvalidToken(){
        String name = "Test";
        String CPF = "9999991";
        String email = "test@gmail.com";
        Long id = 1L;
        List<String> permissions = new ArrayList<>();
        permissions.add("ACCOUNT");
        Date now = new Date();
        Date validity = new Date(now.getTime() + 1);

        String invalidToken = tokenProvider.getAccessToken(name, id, CPF, email, permissions, now, validity);
        Boolean result = tokenProvider.validateToken(invalidToken);
        Assertions.assertFalse(result);
    }

    @Test
    public void getAuthentication_Success(){
        String name = "Test";
        String CPF = "9999991";
        String email = "test@gmail.com";
        Long id = 1L;
        List<String> permissions = new ArrayList<>();
        permissions.add("ACCOUNT");
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliSeconds);

        Account account = new Account();
        account.setEnabled(true);
        account.setId(id);
        account.setEmail(email);
        account.setCPF(CPF);
        account.setAccountNonExpired(true);
        account.setAccountNonLocked(true);
        account.setCredentialsNonExpired(true);
        account.setPassword("12345");
        account.getPermissionsAccount().add("ACCOUNT");

        when(accountRepository.findByNameAndEmailAndCPF(name, email, CPF)).thenReturn(account);
        String tokenTest = tokenProvider.getAccessToken(name, id, CPF, email, permissions, now, validity);
        Authentication auth = tokenProvider.getAuthentication(tokenTest);
        verify(accountRepository, times(1)).findByNameAndEmailAndCPF(name, email, CPF);

        Assertions.assertNotNull(auth);
    }
}