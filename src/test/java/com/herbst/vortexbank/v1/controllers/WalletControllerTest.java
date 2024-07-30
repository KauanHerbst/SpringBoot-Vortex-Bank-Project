package com.herbst.vortexbank.v1.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herbst.vortexbank.util.TransactionStatusEnum;
import com.herbst.vortexbank.util.TransactionTypeEnum;
import com.herbst.vortexbank.v1.dtos.*;
import com.herbst.vortexbank.v1.services.WalletService;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    @BeforeEach
    public void setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).alwaysDo(print()).build();
    }

    @Test
    public void createWalletPassword_Success() throws Exception {
        CreateWalletPasswordDTO dto = new CreateWalletPasswordDTO();
        dto.setCPF("99999999991");
        dto.setName("Test");
        dto.setAccountPassword("123456");
        dto.setWalletKey("WALLET_KEY");
        dto.setWalletPassword("1234");

        StandardResponseDTO responseDTO = new StandardResponseDTO();
        responseDTO.setStatus(202);
        responseDTO.setMessage("Wallet Password Created");

        when(walletService.createWalletPassword(dto)).thenReturn(responseDTO);

        mockMvc.perform(post("/v1/wallet/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(responseDTO.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(responseDTO.getStatus()))
                .andReturn();

        verify(walletService).createWalletPassword(dto);
    }

    @Test
    public void transaction_deposit_success() throws Exception{
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        dto.setCpf("99999999991");
        dto.setName("Test");
        dto.setAmount(157D);
        dto.setDescription("Deposit Test");
        dto.setWalletKey("WALLET_KEY");
        dto.setTransactionType("DEPOSIT");
        dto.setWalletPassword("WALLET_PASSWORD");


        AccountTransactionDTO accountOrigin = new AccountTransactionDTO();
        accountOrigin.setName(dto.getName());
        accountOrigin.setCPF(dto.getCpf());
        accountOrigin.setAccountId(1L);

        TransactionDTO responseDTO = new TransactionDTO();
        responseDTO.setId("TRANSACTION_ID");
        responseDTO.setDescription(dto.getDescription());
        responseDTO.setTransactionType(TransactionTypeEnum.DEPOSIT.getValue());
        responseDTO.setAccountOrigin(accountOrigin);
        responseDTO.setTransactionStatus(TransactionStatusEnum.COMPLETED.getValue());
        responseDTO.setAccountAddressee(accountOrigin);
        responseDTO.setAmount(dto.getAmount());

        when(walletService.transaction(dto)).thenReturn(responseDTO);

        mockMvc.perform(post("/v1/wallet/transaction")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(responseDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionType").value(responseDTO.getTransactionType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionStatus").value(responseDTO.getTransactionStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(responseDTO.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(responseDTO.getAmount()))
                .andReturn();
    }

    @Test
    public void transaction_transfer_success() throws Exception{
        AccountTransactionDTO accountOrigin = new AccountTransactionDTO();
        accountOrigin.setName("Test");
        accountOrigin.setCPF("99999999991");
        accountOrigin.setAccountId(1L);

        AccountTransactionDTO accountAddresse = new AccountTransactionDTO();
        accountAddresse.setAccountId(2L);
        accountAddresse.setName("Test 2");
        accountAddresse.setCPF("99999999992");

        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        dto.setCpf(accountOrigin.getCPF());
        dto.setName(accountOrigin.getName());
        dto.setAmount(10D);
        dto.setDescription("Transfer Test");
        dto.setWalletKey("WALLET_KEY");
        dto.setTransactionType("TRANSFER");
        dto.setWalletPassword("WALLET_PASSWORD");
        dto.setAccountAddresseeCPF(accountAddresse.getCPF());
        dto.setAccountAddresseeName(accountAddresse.getName());


        TransactionDTO responseDTO = new TransactionDTO();
        responseDTO.setId("TRANSACTION_ID");
        responseDTO.setDescription(dto.getDescription());
        responseDTO.setTransactionType(TransactionTypeEnum.TRANSFER.getValue());
        responseDTO.setAccountOrigin(accountOrigin);
        responseDTO.setTransactionStatus(TransactionStatusEnum.COMPLETED.getValue());
        responseDTO.setAccountAddressee(accountAddresse);
        responseDTO.setAmount(dto.getAmount());

        when(walletService.transaction(dto)).thenReturn(responseDTO);

        mockMvc.perform(post("/v1/wallet/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(responseDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionType").value(responseDTO.getTransactionType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionStatus").value(responseDTO.getTransactionStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(responseDTO.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(responseDTO.getAmount()))
                .andReturn();
    }

    @Test
    public void transaction_withdrawal_success() throws Exception{
        AccountTransactionDTO accountOrigin = new AccountTransactionDTO();
        accountOrigin.setName("Test");
        accountOrigin.setCPF("99999999991");
        accountOrigin.setAccountId(1L);

        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        dto.setCpf(accountOrigin.getCPF());
        dto.setName(accountOrigin.getName());
        dto.setAmount(10D);
        dto.setDescription("Withdrawal Test");
        dto.setWalletKey("WALLET_KEY");
        dto.setTransactionType("WITHDRAWAL");
        dto.setWalletPassword("WALLET_PASSWORD");

        TransactionDTO responseDTO = new TransactionDTO();
        responseDTO.setId("TRANSACTION_ID");
        responseDTO.setDescription(dto.getDescription());
        responseDTO.setTransactionType(TransactionTypeEnum.WITHDRAWAL.getValue());
        responseDTO.setAccountOrigin(accountOrigin);
        responseDTO.setTransactionStatus(TransactionStatusEnum.COMPLETED.getValue());
        responseDTO.setAccountAddressee(accountOrigin);
        responseDTO.setAmount(dto.getAmount());

        when(walletService.transaction(dto)).thenReturn(responseDTO);

        mockMvc.perform(post("/v1/wallet/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(responseDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionType").value(responseDTO.getTransactionType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionStatus").value(responseDTO.getTransactionStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(responseDTO.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(responseDTO.getAmount()))
                .andReturn();
    }

    @Test
    public void transaction_charge_success() throws Exception{
        AccountTransactionDTO accountOrigin = new AccountTransactionDTO();
        accountOrigin.setName("Test");
        accountOrigin.setCPF("99999999991");
        accountOrigin.setAccountId(1L);

        AccountTransactionDTO accountAddresse = new AccountTransactionDTO();
        accountAddresse.setAccountId(2L);
        accountAddresse.setName("Test 2");
        accountAddresse.setCPF("99999999992");

        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        dto.setCpf(accountOrigin.getCPF());
        dto.setName(accountOrigin.getName());
        dto.setAmount(100D);
        dto.setDescription("Charge Test");
        dto.setWalletKey("WALLET_KEY");
        dto.setTransactionType("CHARGE");
        dto.setWalletPassword("WALLET_PASSWORD");
        dto.setAccountAddresseeName(accountAddresse.getName());
        dto.setAccountAddresseeCPF(accountAddresse.getCPF());

        TransactionDTO responseDTO = new TransactionDTO();
        responseDTO.setId("TRANSACTION_ID");
        responseDTO.setDescription(dto.getDescription());
        responseDTO.setTransactionType(TransactionTypeEnum.CHARGE.getValue());
        responseDTO.setAccountOrigin(accountOrigin);
        responseDTO.setTransactionStatus(TransactionStatusEnum.PENDING.getValue());
        responseDTO.setAccountAddressee(accountOrigin);
        responseDTO.setAmount(dto.getAmount());
        responseDTO.setIsPaymentDone(false);

        when(walletService.transaction(dto)).thenReturn(responseDTO);

        mockMvc.perform(post("/v1/wallet/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(responseDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionType").value(responseDTO.getTransactionType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionStatus").value(responseDTO.getTransactionStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(responseDTO.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(responseDTO.getAmount()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isPaymentDone").value(responseDTO.getIsPaymentDone()))
                .andReturn();
    }

    @Test
    public void transaction_payment_success() throws Exception{
        AccountTransactionDTO accountOrigin = new AccountTransactionDTO();
        accountOrigin.setName("Test");
        accountOrigin.setCPF("99999999991");
        accountOrigin.setAccountId(1L);

        AccountTransactionDTO accountAddresse = new AccountTransactionDTO();
        accountAddresse.setAccountId(2L);
        accountAddresse.setName("Test 2");
        accountAddresse.setCPF("99999999992");

        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        dto.setCpf(accountAddresse.getCPF());
        dto.setName(accountAddresse.getName());
        dto.setAmount(100D);
        dto.setDescription("Payment Test");
        dto.setWalletKey("WALLET_KEY");
        dto.setTransactionType("PAYMENT");
        dto.setWalletPassword("WALLET_PASSWORD");
        dto.setAccountAddresseeName(accountOrigin.getName());
        dto.setAccountAddresseeCPF(accountOrigin.getCPF());
        dto.setTransactionPaymentId("TRANSACTION_PAYMENT_ID");

        TransactionDTO responseDTO = new TransactionDTO();
        responseDTO.setId("TRANSACTION_ID");
        responseDTO.setDescription(dto.getDescription());
        responseDTO.setTransactionType(TransactionTypeEnum.PAYMENT.getValue());
        responseDTO.setAccountOrigin(accountOrigin);
        responseDTO.setTransactionStatus(TransactionStatusEnum.COMPLETED.getValue());
        responseDTO.setAccountAddressee(accountOrigin);
        responseDTO.setAmount(dto.getAmount());

        when(walletService.transaction(dto)).thenReturn(responseDTO);

        mockMvc.perform(post("/v1/wallet/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(responseDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionType").value(responseDTO.getTransactionType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionStatus").value(responseDTO.getTransactionStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(responseDTO.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(responseDTO.getAmount()))
                .andReturn();
    }

    @Test
    public void transaction_refund_success() throws Exception{
        AccountTransactionDTO accountOrigin = new AccountTransactionDTO();
        accountOrigin.setName("Test");
        accountOrigin.setCPF("99999999991");
        accountOrigin.setAccountId(1L);

        AccountTransactionDTO accountAddresse = new AccountTransactionDTO();
        accountAddresse.setAccountId(2L);
        accountAddresse.setName("Test 2");
        accountAddresse.setCPF("99999999992");

        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        dto.setCpf(accountOrigin.getCPF());
        dto.setName(accountOrigin.getName());
        dto.setAmount(100D);
        dto.setDescription("Refund Test");
        dto.setWalletKey("WALLET_KEY");
        dto.setTransactionType("REFUND");
        dto.setWalletPassword("WALLET_PASSWORD");
        dto.setTransactionRefundId("TRANSACTION_REFUND_ID");

        TransactionDTO responseDTO = new TransactionDTO();
        responseDTO.setId("TRANSACTION_ID");
        responseDTO.setDescription(dto.getDescription());
        responseDTO.setTransactionType(TransactionTypeEnum.REFUND.getValue());
        responseDTO.setAccountOrigin(accountOrigin);
        responseDTO.setTransactionStatus(TransactionStatusEnum.COMPLETED.getValue());
        responseDTO.setAccountAddressee(accountAddresse);
        responseDTO.setAmount(dto.getAmount());

        when(walletService.transaction(dto)).thenReturn(responseDTO);

        mockMvc.perform(post("/v1/wallet/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(responseDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionType").value(responseDTO.getTransactionType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionStatus").value(responseDTO.getTransactionStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(responseDTO.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(responseDTO.getAmount()))
                .andReturn();
    }
}
