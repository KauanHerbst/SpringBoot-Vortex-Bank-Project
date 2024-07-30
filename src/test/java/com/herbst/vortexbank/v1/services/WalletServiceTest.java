package com.herbst.vortexbank.v1.services;

import com.herbst.vortexbank.entities.Account;
import com.herbst.vortexbank.entities.Transaction;
import com.herbst.vortexbank.entities.Wallet;
import com.herbst.vortexbank.exceptions.*;
import com.herbst.vortexbank.repositories.AccountRepository;
import com.herbst.vortexbank.repositories.TransactionFailedRepository;
import com.herbst.vortexbank.repositories.TransactionRepository;
import com.herbst.vortexbank.repositories.WalletRepository;
import com.herbst.vortexbank.security.cryptography.CryptographyAES;
import com.herbst.vortexbank.util.TransactionStatusEnum;
import com.herbst.vortexbank.util.TransactionTypeEnum;
import com.herbst.vortexbank.v1.dtos.CreateWalletPasswordDTO;
import com.herbst.vortexbank.v1.dtos.StandardResponseDTO;
import com.herbst.vortexbank.v1.dtos.TransactionDTO;
import com.herbst.vortexbank.v1.dtos.TransactionReceivedDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CryptographyAES crypto;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionFailedRepository transactionFailedRepository;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createWalletPassword_Success(){
        CreateWalletPasswordDTO dto = new CreateWalletPasswordDTO();
        dto.setName("Test");
        dto.setCPF("10101010101");
        dto.setWalletKey("WALLET_KEY_ENCRYPTED");
        dto.setAccountPassword("ACCOUNT_PASSWORD");
        dto.setWalletPassword("WALLET_PASSWORD");

        Account account = new Account();
        account.setId(1L);
        account.setName(dto.getName());
        account.setEnabled(true);
        account.setPassword("ACCOUNT_PASSWORD");
        account.setWalletKey(dto.getWalletKey());

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setWalletKey("WALLET_KEY");
        wallet.setEnabled(false);
        account.setWallet(wallet);

        when(accountRepository.existsByCPF(dto.getCPF())).thenReturn(true);
        when(accountRepository.findByCPF(dto.getCPF())).thenReturn(account);
        when(crypto.decrypt(account.getWalletKey())).thenReturn("WALLET_KEY");
        when(passwordEncoder.matches(dto.getAccountPassword(), account.getPassword())).thenReturn(true);

        StandardResponseDTO resultDTO = walletService.createWalletPassword(dto);

        Assertions.assertEquals(202, resultDTO.getStatus());
        Assertions.assertEquals("Wallet Password Created", resultDTO.getMessage());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void transaction_deposit_success(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account account = createAccountForTest(1L);
        dto.setName(account.getName());
        dto.setCpf(account.getCPF());
        dto.setAmount(10D);
        Wallet wallet = account.getWallet();
        dto.setWalletPassword(wallet.getPassword());
        dto.setWalletKey(account.getWalletKey());
        dto.setDescription("Test Deposit");
        dto.setTransactionType(TransactionTypeEnum.DEPOSIT.getValue());

        when(accountRepository.findByCPF(account.getCPF())).thenReturn(account);
        when(crypto.decrypt(account.getWalletKey())).thenReturn(account.getWalletKey());
        when(passwordEncoder.matches(dto.getWalletPassword(), wallet.getPassword())).thenReturn(true);

        TransactionDTO transactionDTO = walletService.transaction(dto);
        Assertions.assertEquals("Deposit", transactionDTO.getTransactionType());
        Assertions.assertEquals(account.getName(), transactionDTO.getAccountOrigin().getName());
        Assertions.assertEquals(account.getCPF(), transactionDTO.getAccountOrigin().getCPF());
        Assertions.assertEquals(dto.getAmount(), transactionDTO.getAmount());
    }

    @Test
    public void transaction_deposit_entityNotFound(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account account = createAccountForTest(1L);
        dto.setName(account.getName());
        dto.setCpf(account.getCPF());
        dto.setAmount(10D);
        Wallet wallet = account.getWallet();
        dto.setWalletPassword(wallet.getPassword());
        dto.setWalletKey(account.getWalletKey());
        dto.setDescription("Test Deposit");
        dto.setTransactionType(TransactionTypeEnum.DEPOSIT.getValue());

        when(accountRepository.findByCPF(account.getCPF())).thenReturn(null);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            walletService.transaction(dto);
        });
    }

    @Test
    public void transaction_deposit_accountIsNotActive(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account account = createAccountForTest(1L);
        dto.setName(account.getName());
        dto.setCpf(account.getCPF());
        dto.setAmount(10D);
        Wallet wallet = account.getWallet();
        dto.setWalletPassword(wallet.getPassword());
        dto.setWalletKey(account.getWalletKey());
        dto.setDescription("Test Deposit");
        dto.setTransactionType(TransactionTypeEnum.DEPOSIT.getValue());

        account.setEnabled(false);

        when(accountRepository.findByCPF(account.getCPF())).thenReturn(account);

        Assertions.assertThrows(AccountIsNotActiveException.class, () -> {
            walletService.transaction(dto);
        });
    }

    @Test
    public void transaction_deposit_accountIsExpired(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account account = createAccountForTest(1L);
        dto.setName(account.getName());
        dto.setCpf(account.getCPF());
        dto.setAmount(10D);
        Wallet wallet = account.getWallet();
        dto.setWalletPassword(wallet.getPassword());
        dto.setWalletKey(account.getWalletKey());
        dto.setDescription("Test Deposit");
        dto.setTransactionType(TransactionTypeEnum.DEPOSIT.getValue());

        account.setAccountNonExpired(false);

        when(accountRepository.findByCPF(account.getCPF())).thenReturn(account);

        Assertions.assertThrows(InvalidAccountException.class, () -> {
            walletService.transaction(dto);
        });
    }

    @Test
    public void transaction_deposit_accountIsLocked(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account account = createAccountForTest(1L);
        dto.setName(account.getName());
        dto.setCpf(account.getCPF());
        dto.setAmount(10D);
        Wallet wallet = account.getWallet();
        dto.setWalletPassword(wallet.getPassword());
        dto.setWalletKey(account.getWalletKey());
        dto.setDescription("Test Deposit");
        dto.setTransactionType(TransactionTypeEnum.DEPOSIT.getValue());

        account.setAccountNonLocked(false);

        when(accountRepository.findByCPF(account.getCPF())).thenReturn(account);

        Assertions.assertThrows(InvalidAccountException.class, () -> {
            walletService.transaction(dto);
        });
    }

    @Test
    public void transaction_deposit_walletIsNotActive(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account account = createAccountForTest(1L);
        dto.setName(account.getName());
        dto.setCpf(account.getCPF());
        dto.setAmount(10D);
        Wallet wallet = account.getWallet();
        dto.setWalletPassword(wallet.getPassword());
        dto.setWalletKey(account.getWalletKey());
        dto.setDescription("Test Deposit");
        dto.setTransactionType(TransactionTypeEnum.DEPOSIT.getValue());

        wallet.setEnabled(false);

        when(accountRepository.findByCPF(account.getCPF())).thenReturn(account);

        Assertions.assertThrows(WalletIsNotActiveException.class, () -> {
            walletService.transaction(dto);
        });
    }

    @Test
    public void transaction_deposit_walletIsLocked(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account account = createAccountForTest(1L);
        dto.setName(account.getName());
        dto.setCpf(account.getCPF());
        dto.setAmount(10D);
        Wallet wallet = account.getWallet();
        dto.setWalletPassword(wallet.getPassword());
        dto.setWalletKey(account.getWalletKey());
        dto.setDescription("Test Deposit");
        dto.setTransactionType(TransactionTypeEnum.DEPOSIT.getValue());

        wallet.setWalletNonLocked(false);

        when(accountRepository.findByCPF(account.getCPF())).thenReturn(account);

        Assertions.assertThrows(InvalidAccountException.class, () -> {
            walletService.transaction(dto);
        });
    }


    @Test
    public void transaction_payment_success(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account accountCharged = createAccountForTest(1L);
        Account accountCharging = createAccountForTest(2L);
        dto.setName(accountCharged.getName());
        dto.setCpf(accountCharged.getCPF());
        dto.setAmount(10D);
        Wallet wallet = accountCharged.getWallet();
        dto.setWalletPassword(wallet.getPassword());
        dto.setWalletKey(accountCharged.getWalletKey());
        dto.setDescription("Test Payment");
        dto.setTransactionType(TransactionTypeEnum.PAYMENT.getValue());

        Transaction transactionCharge = new Transaction();
        transactionCharge.setId("ID_TRANSACTION_CHARGE");
        transactionCharge.setIsPaymentDone(false);
        transactionCharge.setAmount(10D);
        transactionCharge.setTransactionStatus(TransactionStatusEnum.PENDING);
        transactionCharge.setTransactionType(TransactionTypeEnum.CHARGE);
        transactionCharge.setAccountAddressee(accountCharged);
        transactionCharge.setAccountOrigin(accountCharging);
        transactionCharge.setIsPaymentDone(false);
        transactionCharge.setDescription("Test Charge");

        dto.setTransactionPaymentId(transactionCharge.getId());

        when(accountRepository.findByCPF(accountCharged.getCPF())).thenReturn(accountCharged);
        when(crypto.decrypt(accountCharged.getWalletKey())).thenReturn(accountCharged.getWalletKey());
        when(passwordEncoder.matches(dto.getWalletPassword(), wallet.getPassword())).thenReturn(true);
        when(transactionRepository.findById(dto.getTransactionPaymentId())).thenReturn(Optional.of(transactionCharge));
        when(accountRepository.findById(transactionCharge.getAccountOrigin().getId())).thenReturn(Optional.of(accountCharging));

        TransactionDTO transactionDTO = walletService.transaction(dto);
        Assertions.assertEquals("Payment", transactionDTO.getTransactionType());
        Assertions.assertEquals(accountCharged.getName(), transactionDTO.getAccountOrigin().getName());
        Assertions.assertEquals(accountCharged.getCPF(), transactionDTO.getAccountOrigin().getCPF());
        Assertions.assertEquals(accountCharging.getName(), transactionDTO.getAccountAddressee().getName());
        Assertions.assertEquals(accountCharging.getCPF(), transactionDTO.getAccountAddressee().getCPF());
        Assertions.assertEquals(dto.getAmount(), transactionDTO.getAmount());

    }

    @Test
    public void transaction_payment_invalidTransactionPaymentId(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account accountCharged = createAccountForTest(1L);
        Account accountCharging = createAccountForTest(2L);
        dto.setName(accountCharged.getName());
        dto.setCpf(accountCharged.getCPF());
        dto.setAmount(10D);
        Wallet wallet = accountCharged.getWallet();
        dto.setWalletPassword(wallet.getPassword());
        dto.setWalletKey(accountCharged.getWalletKey());
        dto.setDescription("Test Payment");
        dto.setTransactionType(TransactionTypeEnum.PAYMENT.getValue());

        Transaction transactionCharge = new Transaction();
        transactionCharge.setId("ID_TRANSACTION_CHARGE");
        transactionCharge.setIsPaymentDone(false);
        transactionCharge.setAmount(10D);
        transactionCharge.setTransactionStatus(TransactionStatusEnum.PENDING);
        transactionCharge.setTransactionType(TransactionTypeEnum.CHARGE);
        transactionCharge.setAccountAddressee(accountCharged);
        transactionCharge.setAccountOrigin(accountCharging);
        transactionCharge.setIsPaymentDone(false);
        transactionCharge.setDescription("Test Charge");

        dto.setTransactionPaymentId(transactionCharge.getId());

        when(accountRepository.findByCPF(accountCharged.getCPF())).thenReturn(accountCharged);
        when(crypto.decrypt(accountCharged.getWalletKey())).thenReturn(accountCharged.getWalletKey());
        when(passwordEncoder.matches(dto.getWalletPassword(), wallet.getPassword())).thenReturn(true);
        when(transactionRepository.findById(dto.getTransactionPaymentId())).thenReturn(Optional.ofNullable(null));
        when(accountRepository.findById(transactionCharge.getAccountOrigin().getId())).thenReturn(Optional.of(accountCharging));

        Assertions.assertThrows(TransactionFailedException.class, () -> {
            walletService.transaction(dto);
        });
    }

    @Test
    public void transaction_payment_insufficientFunds(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account accountCharged = createAccountForTest(1L);
        Account accountCharging = createAccountForTest(2L);
        dto.setName(accountCharged.getName());
        dto.setCpf(accountCharged.getCPF());
        dto.setAmount(1000D);
        Wallet wallet = accountCharged.getWallet();
        dto.setWalletPassword(wallet.getPassword());
        dto.setWalletKey(accountCharged.getWalletKey());
        dto.setDescription("Test Payment");
        dto.setTransactionType(TransactionTypeEnum.PAYMENT.getValue());

        Transaction transactionCharge = new Transaction();
        transactionCharge.setId("ID_TRANSACTION_CHARGE");
        transactionCharge.setIsPaymentDone(false);
        transactionCharge.setAmount(1000D);
        transactionCharge.setTransactionStatus(TransactionStatusEnum.PENDING);
        transactionCharge.setTransactionType(TransactionTypeEnum.CHARGE);
        transactionCharge.setAccountAddressee(accountCharged);
        transactionCharge.setAccountOrigin(accountCharging);
        transactionCharge.setIsPaymentDone(false);
        transactionCharge.setDescription("Test Charge");

        dto.setTransactionPaymentId(transactionCharge.getId());

        when(accountRepository.findByCPF(accountCharged.getCPF())).thenReturn(accountCharged);
        when(crypto.decrypt(accountCharged.getWalletKey())).thenReturn(accountCharged.getWalletKey());
        when(passwordEncoder.matches(dto.getWalletPassword(), wallet.getPassword())).thenReturn(true);
        when(transactionRepository.findById(dto.getTransactionPaymentId())).thenReturn(Optional.of(transactionCharge));
        when(accountRepository.findById(transactionCharge.getAccountOrigin().getId())).thenReturn(Optional.of(accountCharging));

        Assertions.assertThrows(TransactionFailedException.class, () -> {
            walletService.transaction(dto);
        });
    }

    @Test
    public void transaction_payment_chargeAlreadyPaid(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account accountCharged = createAccountForTest(1L);
        Account accountCharging = createAccountForTest(2L);
        dto.setName(accountCharged.getName());
        dto.setCpf(accountCharged.getCPF());
        dto.setAmount(10D);
        Wallet wallet = accountCharged.getWallet();
        dto.setWalletPassword(wallet.getPassword());
        dto.setWalletKey(accountCharged.getWalletKey());
        dto.setDescription("Test Payment");
        dto.setTransactionType(TransactionTypeEnum.PAYMENT.getValue());

        Transaction transactionCharge = new Transaction();
        transactionCharge.setId("ID_TRANSACTION_CHARGE");
        transactionCharge.setIsPaymentDone(false);
        transactionCharge.setAmount(10D);
        transactionCharge.setTransactionStatus(TransactionStatusEnum.COMPLETED);
        transactionCharge.setTransactionType(TransactionTypeEnum.CHARGE);
        transactionCharge.setAccountAddressee(accountCharged);
        transactionCharge.setAccountOrigin(accountCharging);
        transactionCharge.setIsPaymentDone(false);
        transactionCharge.setDescription("Test Charge");

        dto.setTransactionPaymentId(transactionCharge.getId());

        when(accountRepository.findByCPF(accountCharged.getCPF())).thenReturn(accountCharged);
        when(crypto.decrypt(accountCharged.getWalletKey())).thenReturn(accountCharged.getWalletKey());
        when(passwordEncoder.matches(dto.getWalletPassword(), wallet.getPassword())).thenReturn(true);
        when(transactionRepository.findById(dto.getTransactionPaymentId())).thenReturn(Optional.of(transactionCharge));
        when(accountRepository.findById(transactionCharge.getAccountOrigin().getId())).thenReturn(Optional.of(accountCharging));

        Assertions.assertThrows(TransactionFailedException.class, () -> {
            walletService.transaction(dto);
        });
    }

    @Test
    public void transaction_charge_success(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account accountCharged = createAccountForTest(1L);
        Account accountCharging = createAccountForTest(2L);
        dto.setName(accountCharging.getName());
        dto.setCpf(accountCharging.getCPF());
        dto.setAmount(100D);
        Wallet wallet = accountCharging.getWallet();
        dto.setWalletPassword(wallet.getPassword());
        dto.setWalletKey(accountCharging.getWalletKey());
        dto.setDescription("Test charge");
        dto.setAccountAddresseeCPF(accountCharged.getCPF());
        dto.setAccountAddresseeName(accountCharged.getName());
        dto.setTransactionType(TransactionTypeEnum.CHARGE.getValue());

        when(accountRepository.findByCPF(accountCharging.getCPF())).thenReturn(accountCharging);
        when(accountRepository.findByCPF(accountCharged.getCPF())).thenReturn(accountCharged);
        when(crypto.decrypt(accountCharging.getWalletKey())).thenReturn(accountCharging.getWalletKey());
        when(passwordEncoder.matches(dto.getWalletPassword(), wallet.getPassword())).thenReturn(true);

        TransactionDTO transactionDTOResult = walletService.transaction(dto);
        Assertions.assertEquals(dto.getTransactionType(), transactionDTOResult.getTransactionType());
        Assertions.assertEquals(dto.getAmount(), transactionDTOResult.getAmount());
        Assertions.assertEquals(dto.getDescription(), transactionDTOResult.getDescription());
        Assertions.assertEquals(dto.getName(), transactionDTOResult.getAccountOrigin().getName());
        Assertions.assertEquals(dto.getCpf(), transactionDTOResult.getAccountOrigin().getCPF());
    }

    @Test
    public void transaction_charge_entityNotFound(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account accountCharged = createAccountForTest(1L);
        Account accountCharging = createAccountForTest(2L);
        dto.setName(accountCharging.getName());
        dto.setCpf(accountCharging.getCPF());
        dto.setAmount(100D);
        Wallet wallet = accountCharging.getWallet();
        dto.setWalletPassword(wallet.getPassword());
        dto.setWalletKey(accountCharging.getWalletKey());
        dto.setDescription("Test charge");
        dto.setAccountAddresseeCPF(accountCharged.getCPF());
        dto.setAccountAddresseeName(accountCharged.getName());
        dto.setTransactionType(TransactionTypeEnum.CHARGE.getValue());

        when(accountRepository.findByCPF(accountCharging.getCPF())).thenReturn(accountCharging);
        when(accountRepository.findByCPF(accountCharged.getCPF())).thenReturn(null);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            walletService.transaction(dto);
        });

    }

    @Test
    public void transaction_withdrawal_success(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account account = createAccountForTest(1L);
        dto.setName(account.getName());
        dto.setCpf(account.getCPF());
        dto.setAmount(100D);
        Wallet wallet = account.getWallet();
        dto.setWalletPassword(wallet.getPassword());
        dto.setWalletKey(account.getWalletKey());
        dto.setDescription("Test withdrawal");
        dto.setAccountAddresseeCPF(account.getCPF());
        dto.setAccountAddresseeName(account.getName());
        dto.setTransactionType(TransactionTypeEnum.WITHDRAWAL.getValue());


        when(accountRepository.findByCPF(account.getCPF())).thenReturn(account);
        when(crypto.decrypt(account.getWalletKey())).thenReturn(account.getWalletKey());
        when(passwordEncoder.matches(dto.getWalletPassword(), wallet.getPassword())).thenReturn(true);

        TransactionDTO transactionDTOResult = walletService.transaction(dto);

        Assertions.assertEquals(dto.getTransactionType(), transactionDTOResult.getTransactionType());
        Assertions.assertEquals(dto.getCpf(), transactionDTOResult.getAccountOrigin().getCPF());
        Assertions.assertEquals(dto.getName(), transactionDTOResult.getAccountOrigin().getName());
        Assertions.assertEquals(dto.getAmount(), transactionDTOResult.getAmount());
        Assertions.assertEquals(dto.getDescription(), transactionDTOResult.getDescription());
    }

    @Test
    public void transaction_withdrawal_entityNotFound(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account account = createAccountForTest(1L);
        dto.setName(account.getName());
        dto.setCpf(account.getCPF());
        dto.setAmount(100D);
        Wallet wallet = account.getWallet();
        dto.setWalletPassword(wallet.getPassword());
        dto.setWalletKey(account.getWalletKey());
        dto.setDescription("Test withdrawal");
        dto.setAccountAddresseeCPF(account.getCPF());
        dto.setAccountAddresseeName(account.getName());
        dto.setTransactionType(TransactionTypeEnum.WITHDRAWAL.getValue());

        when(accountRepository.findByCPF(account.getCPF())).thenReturn(null);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            walletService.transaction(dto);
        });

    }

    @Test
    public void transaction_withdrawal_invalidPassword(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account account = createAccountForTest(1L);
        dto.setName(account.getName());
        dto.setCpf(account.getCPF());
        dto.setAmount(100D);
        Wallet wallet = account.getWallet();
        dto.setWalletPassword(wallet.getPassword());
        dto.setWalletKey(account.getWalletKey());
        dto.setDescription("Test withdrawal");
        dto.setWalletPassword(wallet.getPassword());
        dto.setAccountAddresseeCPF(account.getCPF());
        dto.setAccountAddresseeName(account.getName());
        dto.setTransactionType(TransactionTypeEnum.WITHDRAWAL.getValue());

        when(accountRepository.findByCPF(account.getCPF())).thenReturn(account);
        when(crypto.decrypt(account.getWalletKey())).thenReturn(account.getWalletKey());
        when(passwordEncoder.matches(dto.getWalletPassword(), wallet.getPassword())).thenReturn(false);

        Assertions.assertThrows(BadCredentialsException.class, () -> {
            walletService.transaction(dto);
        });
    }

    @Test
    public void transaction_transfer_success(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account accountOrigin = createAccountForTest(1L);
        Account accountAddressee = createAccountForTest(2L);
        dto.setTransactionType(TransactionTypeEnum.TRANSFER.getValue());
        dto.setWalletKey(accountOrigin.getWalletKey());
        dto.setCpf(accountOrigin.getCPF());
        dto.setName(accountOrigin.getName());
        dto.setWalletPassword(accountOrigin.getWallet().getPassword());
        dto.setDescription("Transfer Test");
        dto.setAccountAddresseeName(accountAddressee.getName());
        dto.setAccountAddresseeCPF(accountAddressee.getCPF());
        dto.setAmount(50D);

        when(accountRepository.findByCPF(accountAddressee.getCPF())).thenReturn(accountAddressee);
        when(accountRepository.findByCPF(accountOrigin.getCPF())).thenReturn(accountOrigin);
        when(crypto.decrypt(accountOrigin.getWalletKey())).thenReturn(accountOrigin.getWalletKey());
        when(passwordEncoder.matches(dto.getWalletPassword(), accountOrigin.getWallet().getPassword())).thenReturn(true);

        TransactionDTO transactionDTOResult = walletService.transaction(dto);

        Assertions.assertEquals(dto.getName(), transactionDTOResult.getAccountOrigin().getName());
        Assertions.assertEquals(dto.getCpf(), transactionDTOResult.getAccountOrigin().getCPF());
        Assertions.assertEquals(dto.getAmount(), transactionDTOResult.getAmount());
        Assertions.assertEquals(dto.getTransactionType(), transactionDTOResult.getTransactionType());
        Assertions.assertEquals(dto.getAccountAddresseeCPF(), transactionDTOResult.getAccountAddressee().getCPF());
        Assertions.assertEquals(dto.getAccountAddresseeName(), transactionDTOResult.getAccountAddressee().getName());
        Assertions.assertEquals(150D, accountAddressee.getWallet().getBalance());
        Assertions.assertEquals(50D, accountOrigin.getWallet().getBalance());
    }

    @Test
    public void transaction_transfer_invalidWallet(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account accountOrigin = createAccountForTest(1L);
        Account accountAddressee = createAccountForTest(2L);
        dto.setTransactionType(TransactionTypeEnum.TRANSFER.getValue());
        dto.setWalletKey("INVALID_WALLET");
        dto.setCpf(accountOrigin.getCPF());
        dto.setName(accountOrigin.getName());
        dto.setWalletPassword(accountOrigin.getWallet().getPassword());
        dto.setDescription("Transfer Test");
        dto.setAccountAddresseeName(accountAddressee.getName());
        dto.setAccountAddresseeCPF(accountAddressee.getCPF());
        dto.setAmount(50D);

        when(accountRepository.findByCPF(accountAddressee.getCPF())).thenReturn(accountAddressee);
        when(accountRepository.findByCPF(accountOrigin.getCPF())).thenReturn(accountOrigin);
        when(crypto.decrypt(dto.getWalletKey())).thenReturn(dto.getWalletKey());
        when(passwordEncoder.matches(dto.getWalletPassword(), accountOrigin.getWallet().getPassword())).thenReturn(true);

        Assertions.assertThrows(InvalidWalletKeyException.class, () -> walletService.transaction(dto));
    }

    @Test
    public void transaction_transfer_invalidPassword(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account accountOrigin = createAccountForTest(1L);
        Account accountAddressee = createAccountForTest(2L);
        dto.setTransactionType(TransactionTypeEnum.TRANSFER.getValue());
        dto.setWalletKey(accountOrigin.getWalletKey());
        dto.setCpf(accountOrigin.getCPF());
        dto.setName(accountOrigin.getName());
        dto.setWalletPassword(accountOrigin.getWallet().getPassword());
        dto.setDescription("Transfer Test");
        dto.setAccountAddresseeName(accountAddressee.getName());
        dto.setAccountAddresseeCPF(accountAddressee.getCPF());
        dto.setAmount(50D);

        when(accountRepository.findByCPF(accountAddressee.getCPF())).thenReturn(accountAddressee);
        when(accountRepository.findByCPF(accountOrigin.getCPF())).thenReturn(accountOrigin);
        when(crypto.decrypt(dto.getWalletKey())).thenReturn(dto.getWalletKey());
        when(passwordEncoder.matches(dto.getWalletPassword(), accountOrigin.getWallet().getPassword())).thenReturn(false);

        Assertions.assertThrows(BadCredentialsException.class, () -> walletService.transaction(dto));
    }

    @Test
    public void transaction_transfer_insufficientFunds(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account accountOrigin = createAccountForTest(1L);
        Account accountAddressee = createAccountForTest(2L);
        dto.setTransactionType(TransactionTypeEnum.TRANSFER.getValue());
        dto.setWalletKey(accountOrigin.getWalletKey());
        dto.setCpf(accountOrigin.getCPF());
        dto.setName(accountOrigin.getName());
        dto.setWalletPassword(accountOrigin.getWallet().getPassword());
        dto.setDescription("Transfer Test");
        dto.setAccountAddresseeName(accountAddressee.getName());
        dto.setAccountAddresseeCPF(accountAddressee.getCPF());
        dto.setAmount(150D);

        when(accountRepository.findByCPF(accountAddressee.getCPF())).thenReturn(accountAddressee);
        when(accountRepository.findByCPF(accountOrigin.getCPF())).thenReturn(accountOrigin);
        when(crypto.decrypt(dto.getWalletKey())).thenReturn(dto.getWalletKey());
        when(passwordEncoder.matches(dto.getWalletPassword(), accountOrigin.getWallet().getPassword())).thenReturn(true);

        Assertions.assertThrows(TransactionFailedException.class, () -> walletService.transaction(dto));
    }

    @Test
    public void transaction_refund_success(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account accountRequestedRefund = createAccountForTest(1L);
        Account accountRefunded = createAccountForTest(2L);

        Transaction transactionTransfer = new Transaction();
        transactionTransfer.setAccountAddressee(accountRefunded);
        transactionTransfer.setAccountOrigin(accountRequestedRefund);
        transactionTransfer.setAmount(50D);
        transactionTransfer.setId("ID_TRANSACTION_TRANSFER");
        transactionTransfer.setTransactionType(TransactionTypeEnum.TRANSFER);
        transactionTransfer.setTransactionStatus(TransactionStatusEnum.COMPLETED);
        transactionTransfer.setTransactionCreatedAt(new Date());
        transactionTransfer.setDescription("Transfer for refund test");

        dto.setAmount(transactionTransfer.getAmount());
        dto.setTransactionRefundId(transactionTransfer.getId());
        dto.setName(accountRequestedRefund.getName());
        dto.setCpf(accountRequestedRefund.getCPF());
        dto.setDescription("Refund Test");
        dto.setWalletPassword(accountRequestedRefund.getWallet().getPassword());
        dto.setTransactionType(TransactionTypeEnum.REFUND.getValue());
        dto.setWalletKey(accountRequestedRefund.getWalletKey());
        dto.setAccountAddresseeName(accountRefunded.getName());
        dto.setAccountAddresseeCPF(accountRefunded.getCPF());

        when(accountRepository.findByCPF(accountRequestedRefund.getCPF())).thenReturn(accountRequestedRefund);
        when(transactionRepository.findById(transactionTransfer.getId())).thenReturn(Optional.of(transactionTransfer));
        when(accountRepository.findByCPF(accountRefunded.getCPF())).thenReturn(accountRefunded);
        when(crypto.decrypt(dto.getWalletKey())).thenReturn(dto.getWalletKey());
        when(passwordEncoder.matches(dto.getWalletPassword(), accountRequestedRefund.getWallet().getPassword())).thenReturn(true);

        TransactionDTO transactionDTOResult = walletService.transaction(dto);

        Assertions.assertEquals(dto.getAmount(), transactionDTOResult.getAmount());
        Assertions.assertEquals(dto.getTransactionRefundId(), transactionDTOResult.getTransactionRefundId());
        Assertions.assertEquals(TransactionStatusEnum.COMPLETED.getValue(), transactionDTOResult.getTransactionStatus());
        Assertions.assertEquals(dto.getAccountAddresseeName(), transactionDTOResult.getAccountOrigin().getName());
        Assertions.assertEquals(dto.getAccountAddresseeCPF(), transactionDTOResult.getAccountOrigin().getCPF());
        Assertions.assertEquals(dto.getName(), transactionDTOResult.getAccountAddressee().getName());
        Assertions.assertEquals(dto.getCpf(), transactionDTOResult.getAccountAddressee().getCPF());
        Assertions.assertEquals(TransactionStatusEnum.REFUNDED.getValue(), transactionTransfer.getTransactionStatus());
        Assertions.assertEquals(150D, accountRequestedRefund.getWallet().getBalance());
        Assertions.assertEquals(50D, accountRefunded.getWallet().getBalance());
    }

    @Test
    public void transaction_refund_invalidTransactionRefundId(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account accountRequestedRefund = createAccountForTest(1L);
        Account accountRefunded = createAccountForTest(2L);

        Transaction transactionTransfer = new Transaction();
        transactionTransfer.setAccountAddressee(accountRefunded);
        transactionTransfer.setAccountOrigin(accountRequestedRefund);
        transactionTransfer.setAmount(50D);
        transactionTransfer.setId("ID_TRANSACTION_TRANSFER");
        transactionTransfer.setTransactionType(TransactionTypeEnum.TRANSFER);
        transactionTransfer.setTransactionStatus(TransactionStatusEnum.COMPLETED);
        transactionTransfer.setTransactionCreatedAt(new Date());
        transactionTransfer.setDescription("Transfer for refund test");

        dto.setAmount(transactionTransfer.getAmount());
        dto.setTransactionRefundId("INVALID_ID");
        dto.setName(accountRequestedRefund.getName());
        dto.setCpf(accountRequestedRefund.getCPF());
        dto.setDescription("Refund Test");
        dto.setWalletPassword(accountRequestedRefund.getWallet().getPassword());
        dto.setTransactionType(TransactionTypeEnum.REFUND.getValue());
        dto.setWalletKey(accountRequestedRefund.getWalletKey());
        dto.setAccountAddresseeName(accountRefunded.getName());
        dto.setAccountAddresseeCPF(accountRefunded.getCPF());

        when(accountRepository.findByCPF(accountRequestedRefund.getCPF())).thenReturn(accountRequestedRefund);
        when(transactionRepository.findById(transactionTransfer.getId())).thenReturn(Optional.ofNullable(null));
        when(accountRepository.findByCPF(accountRefunded.getCPF())).thenReturn(accountRefunded);
        when(crypto.decrypt(dto.getWalletKey())).thenReturn(dto.getWalletKey());
        when(passwordEncoder.matches(dto.getWalletPassword(), accountRequestedRefund.getWallet().getPassword())).thenReturn(true);

        Assertions.assertThrows(InvalidTransactionException.class, () -> walletService.transaction(dto));
    }

    @Test
    public void transaction_refund_TransactionWasNotCompleted(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account accountRequestedRefund = createAccountForTest(1L);
        Account accountRefunded = createAccountForTest(2L);

        Transaction transactionTransfer = new Transaction();
        transactionTransfer.setAccountAddressee(accountRefunded);
        transactionTransfer.setAccountOrigin(accountRequestedRefund);
        transactionTransfer.setAmount(50D);
        transactionTransfer.setId("ID_TRANSACTION_TRANSFER");
        transactionTransfer.setTransactionType(TransactionTypeEnum.TRANSFER);
        transactionTransfer.setTransactionStatus(TransactionStatusEnum.PENDING);
        transactionTransfer.setTransactionCreatedAt(new Date());
        transactionTransfer.setDescription("Transfer for refund test");

        dto.setAmount(transactionTransfer.getAmount());
        dto.setTransactionRefundId(transactionTransfer.getId());
        dto.setName(accountRequestedRefund.getName());
        dto.setCpf(accountRequestedRefund.getCPF());
        dto.setDescription("Refund Test");
        dto.setWalletPassword(accountRequestedRefund.getWallet().getPassword());
        dto.setTransactionType(TransactionTypeEnum.REFUND.getValue());
        dto.setWalletKey(accountRequestedRefund.getWalletKey());
        dto.setAccountAddresseeName(accountRefunded.getName());
        dto.setAccountAddresseeCPF(accountRefunded.getCPF());

        when(accountRepository.findByCPF(accountRequestedRefund.getCPF())).thenReturn(accountRequestedRefund);
        when(transactionRepository.findById(transactionTransfer.getId())).thenReturn(Optional.of(transactionTransfer));
        when(accountRepository.findByCPF(accountRefunded.getCPF())).thenReturn(accountRefunded);
        when(crypto.decrypt(dto.getWalletKey())).thenReturn(dto.getWalletKey());
        when(passwordEncoder.matches(dto.getWalletPassword(), accountRequestedRefund.getWallet().getPassword())).thenReturn(true);

        Assertions.assertThrows(InvalidTransactionException.class, () -> walletService.transaction(dto));
    }

    @Test
    public void transaction_refund_transactionIsRefunded(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account accountRequestedRefund = createAccountForTest(1L);
        Account accountRefunded = createAccountForTest(2L);

        Transaction transactionTransfer = new Transaction();
        transactionTransfer.setAccountAddressee(accountRefunded);
        transactionTransfer.setAccountOrigin(accountRequestedRefund);
        transactionTransfer.setAmount(50D);
        transactionTransfer.setId("ID_TRANSACTION_TRANSFER");
        transactionTransfer.setTransactionType(TransactionTypeEnum.TRANSFER);
        transactionTransfer.setTransactionStatus(TransactionStatusEnum.REFUNDED);
        transactionTransfer.setTransactionCreatedAt(new Date());
        transactionTransfer.setDescription("Transfer for refund test");

        dto.setAmount(transactionTransfer.getAmount());
        dto.setTransactionRefundId(transactionTransfer.getId());
        dto.setName(accountRequestedRefund.getName());
        dto.setCpf(accountRequestedRefund.getCPF());
        dto.setDescription("Refund Test");
        dto.setWalletPassword(accountRequestedRefund.getWallet().getPassword());
        dto.setTransactionType(TransactionTypeEnum.REFUND.getValue());
        dto.setWalletKey(accountRequestedRefund.getWalletKey());
        dto.setAccountAddresseeName(accountRefunded.getName());
        dto.setAccountAddresseeCPF(accountRefunded.getCPF());

        when(accountRepository.findByCPF(accountRequestedRefund.getCPF())).thenReturn(accountRequestedRefund);
        when(transactionRepository.findById(transactionTransfer.getId())).thenReturn(Optional.of(transactionTransfer));
        when(accountRepository.findByCPF(accountRefunded.getCPF())).thenReturn(accountRefunded);
        when(crypto.decrypt(dto.getWalletKey())).thenReturn(dto.getWalletKey());
        when(passwordEncoder.matches(dto.getWalletPassword(), accountRequestedRefund.getWallet().getPassword())).thenReturn(true);

        Assertions.assertThrows(InvalidTransactionException.class, () -> walletService.transaction(dto));
    }

    @Test
    public void transaction_refund_transactionTypeCannotBeRefunded(){
        TransactionReceivedDTO dto = new TransactionReceivedDTO();
        Account accountRequestedRefund = createAccountForTest(1L);
        Account accountRefunded = createAccountForTest(2L);

        Transaction transactionTransfer = new Transaction();
        transactionTransfer.setAccountAddressee(accountRefunded);
        transactionTransfer.setAccountOrigin(accountRequestedRefund);
        transactionTransfer.setAmount(50D);
        transactionTransfer.setId("ID_TRANSACTION_DEPOSIT");
        transactionTransfer.setTransactionType(TransactionTypeEnum.DEPOSIT);
        transactionTransfer.setTransactionStatus(TransactionStatusEnum.COMPLETED);
        transactionTransfer.setTransactionCreatedAt(new Date());
        transactionTransfer.setDescription("Deposit for refund test");

        dto.setAmount(transactionTransfer.getAmount());
        dto.setTransactionRefundId(transactionTransfer.getId());
        dto.setName(accountRequestedRefund.getName());
        dto.setCpf(accountRequestedRefund.getCPF());
        dto.setDescription("Refund Test");
        dto.setWalletPassword(accountRequestedRefund.getWallet().getPassword());
        dto.setTransactionType(TransactionTypeEnum.REFUND.getValue());
        dto.setWalletKey(accountRequestedRefund.getWalletKey());
        dto.setAccountAddresseeName(accountRefunded.getName());
        dto.setAccountAddresseeCPF(accountRefunded.getCPF());

        when(accountRepository.findByCPF(accountRequestedRefund.getCPF())).thenReturn(accountRequestedRefund);
        when(transactionRepository.findById(transactionTransfer.getId())).thenReturn(Optional.of(transactionTransfer));
        when(accountRepository.findByCPF(accountRefunded.getCPF())).thenReturn(accountRefunded);
        when(crypto.decrypt(dto.getWalletKey())).thenReturn(dto.getWalletKey());
        when(passwordEncoder.matches(dto.getWalletPassword(), accountRequestedRefund.getWallet().getPassword())).thenReturn(true);

        Assertions.assertThrows(InvalidTransactionException.class, () -> walletService.transaction(dto));
    }

    private Account createAccountForTest(Long id){
        Account account = new Account();
        Date date = new Date();
        account.setId(id);
        account.setName("Test " + id);
        account.setEmail("test" + id + "@gmail.com");
        account.setCPF("9999999999" + id);
        account.setTelephone("99999999" + id);
        account.setAccountCreatedAt(date);
        account.setPassword("ACCOUNT_PASSWORD");
        account.setEnabled(true);
        account.setAccountNonLocked(true);
        account.setAccountNonExpired(true);
        account.setCredentialsNonExpired(true);
        account.setDateOfBirth("03/06/2004");
        account.setWalletKey("WALLET_KEY");

        Wallet wallet = new Wallet();
        wallet.setBalance(100D);
        wallet.setWalletCreatedAt(date);
        wallet.setWalletNonLocked(true);
        wallet.setEnabled(true);
        wallet.setId(id);
        wallet.setWalletKey("WALLET_KEY");
        wallet.setTypeOfCurrency("Real");
        wallet.setPassword("WALLET_PASSWORD");

        account.setWallet(wallet);

        return account;
    }


}