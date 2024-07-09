package com.herbst.vortexbank.v1.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.herbst.vortexbank.v1.dtos.*;
import com.herbst.vortexbank.v1.services.AccountService;
import com.herbst.vortexbank.v1.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    public void setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).alwaysDo(print()).build();
    }

    @Test
    public void createAccount_Success() throws Exception {
        CreateAccountDTO dto = new CreateAccountDTO();
        dto.setName("Test");
        dto.setCPF("99999991");
        dto.setEmail("test@gmail.com");
        dto.setTelephone("10101010");
        dto.setDateOfBirth("17/07/2004");

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountId(1L);
        accountDTO.setCPF(dto.getCPF());
        accountDTO.setEmail(dto.getEmail());
        accountDTO.setName(dto.getName());
        accountDTO.setDateOfBirth(dto.getDateOfBirth());
        accountDTO.setTelephone(dto.getTelephone());
        accountDTO.setAccountCreatedAt(new Date());
        accountDTO.setEnabled(false);
        accountDTO.setAccountNonExpired(true);
        accountDTO.setAccountNonLocked(true);
        accountDTO.setCredentialsNonExpired(true);

        when(authService.create(dto)).thenReturn(accountDTO);

        mockMvc.perform(post("/v1/account")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(accountDTO.getAccountId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value(accountDTO.getCPF()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(accountDTO.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(accountDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enabled").value(accountDTO.getEnabled()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountNonLocked")
                        .value(accountDTO.getAccountNonLocked()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.credentialsNonExpired")
                        .value(accountDTO.getCredentialsNonExpired()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountNonExpired")
                        .value(accountDTO.getAccountNonExpired()))
                .andReturn();

        verify(authService).create(dto);
    }

    @Test
    public void createAccountPassword_Success() throws Exception {
        CreateAccountPasswordDTO dto = new CreateAccountPasswordDTO();
        dto.setName("Test");
        dto.setEmail("test@gmail.com");
        dto.setCPF("999999991");
        dto.setPassword("123456");

        when(accountService.createAccountPassword(dto)).thenReturn(true);

        mockMvc.perform(post("/v1/account/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                        .andExpect(MockMvcResultMatchers.status().isAccepted())
                                .andReturn();

        verify(accountService).createAccountPassword(dto);
    }

    @Test
    public void createAccountPassword_NotFound() throws Exception {
        CreateAccountPasswordDTO dto = new CreateAccountPasswordDTO();
        dto.setName("Test");
        dto.setEmail("test@gmail.com");
        dto.setCPF("999999991");
        dto.setPassword("123456");

        when(accountService.createAccountPassword(dto)).thenReturn(false);

        mockMvc.perform(post("/v1/account/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        verify(accountService).createAccountPassword(dto);
    }

    @Test
    public void signIn_Success() throws Exception{
        AccountSignInDTO dto = new AccountSignInDTO();
        dto.setCPF("9999991");
        dto.setPassword("123456");

        Date now = new Date();

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setName("Test");
        tokenDTO.setAuthenticated(true);
        tokenDTO.setAccessToken("accessToken");
        tokenDTO.setRefreshToken("refreshToken");
        tokenDTO.setCreated(now);
        tokenDTO.setExpiration(new Date(now.getTime() + 3600000));

        when(authService.signin(dto)).thenReturn(tokenDTO);

        mockMvc.perform(post("/v1/account/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(tokenDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authenticated").value(tokenDTO.getAuthenticated()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value(tokenDTO.getAccessToken()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value(tokenDTO.getRefreshToken()))
                .andReturn();

        verify(authService).signin(dto);
    }

    @Test
    public void changeAccountPassword_Success() throws Exception{
        ChangeAccountPasswordDTO dto = new ChangeAccountPasswordDTO();
        dto.setName("Test");
        dto.setEmail("test@gmail.com");
        dto.setCPF("9999991");
        dto.setPassword("123456");
        dto.setNewPassword("654321");

        when(accountService.changeAccountPassword(dto)).thenReturn(true);

        mockMvc.perform(post("/v1/account/newpassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andReturn();

        verify(accountService).changeAccountPassword(dto);
    }

    @Test
    public void changeAccountPassword_NotFound() throws Exception {
        ChangeAccountPasswordDTO dto = new ChangeAccountPasswordDTO();
        dto.setName("Test");
        dto.setEmail("test@gmail.com");
        dto.setCPF("9999991");
        dto.setPassword("123456");
        dto.setNewPassword("654321");

        when(accountService.changeAccountPassword(dto)).thenReturn(false);

        mockMvc.perform(post("/v1/account/newpassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        verify(accountService).changeAccountPassword(dto);
    }
}