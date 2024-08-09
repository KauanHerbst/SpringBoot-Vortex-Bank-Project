package com.herbst.vortexbank.v1.services;

import com.herbst.vortexbank.entities.Account;
import com.herbst.vortexbank.entities.Transaction;
import com.herbst.vortexbank.entities.TransactionFailed;
import com.herbst.vortexbank.entities.Wallet;
import com.herbst.vortexbank.exceptions.*;
import com.herbst.vortexbank.mapper.CustomMapperTransaction;
import com.herbst.vortexbank.repositories.AccountRepository;
import com.herbst.vortexbank.repositories.TransactionFailedRepository;
import com.herbst.vortexbank.repositories.TransactionRepository;
import com.herbst.vortexbank.repositories.WalletRepository;
import com.herbst.vortexbank.security.cryptography.CryptographyAES;
import com.herbst.vortexbank.util.NotificationTypeEnum;
import com.herbst.vortexbank.util.TransactionStatusEnum;
import com.herbst.vortexbank.util.TransactionTypeEnum;
import com.herbst.vortexbank.v1.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CryptographyAES crypto;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionFailedRepository transactionFailedRepository;

    @Autowired
    private NotificationService notificationService;

    public StandardResponseDTO createWalletPassword(CreateWalletPasswordDTO data){
        if(data.getWalletKey().isBlank()) throw new InvalidWalletKeyException();
        if(!accountRepository.existsByCPF(data.getCPF())) throw new EntityNotFoundException();
        Account account = accountRepository.findByCPF(data.getCPF());
        Wallet wallet = account.getWallet();

        String encryptedWalletKey = data.getWalletKey();
        String accountWalletKey = wallet.getWalletKey();
        Boolean isValidKey = isValidWalletKey(encryptedWalletKey, accountWalletKey);
        if(!isValidKey) throw new InvalidWalletKeyException();

        if(!passwordEncoder.matches(data.getAccountPassword(), account.getPassword())) throw
                new BadCredentialsException("Invalid Account Password");
        if(!account.getEnabled()) throw new AccountIsNotActiveException();
        if(wallet.getEnabled()) throw new WalletIsActiveException();
        wallet.setEnabled(true);
        wallet.setPassword(passwordEncoder.encode(data.getWalletPassword()));
        wallet.setTypeOfCurrency("Real");
        accountRepository.save(account);

        String message = "Wallet Password Created";
        HttpStatus status = HttpStatus.ACCEPTED;
        return new StandardResponseDTO(message, status.value());
    }

    public TransactionDTO getTransaction(String id){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return CustomMapperTransaction.mapperObject(transaction);
    }

    public TransactionDTO transaction(TransactionReceivedDTO transactionReceivedDTO){
        TransactionTypeEnum transactionType = TransactionTypeEnum.fromValue(transactionReceivedDTO.getTransactionType());
        TransactionDTO transactionDTO;

        switch (transactionType){
            case TRANSFER -> transactionDTO = transfer(transactionReceivedDTO);
            case REFUND -> transactionDTO = refund(transactionReceivedDTO);
            case WITHDRAWAL -> transactionDTO = withdrawal(transactionReceivedDTO);
            case DEPOSIT -> transactionDTO = deposit(transactionReceivedDTO);
            case CHARGE -> transactionDTO = charge(transactionReceivedDTO);
            case PAYMENT -> transactionDTO = payment(transactionReceivedDTO);
            case null, default -> throw new InvalidTransactionException("Invalid Transaction Type Value");
        }

        return transactionDTO;
    }

    private TransactionDTO refund(TransactionReceivedDTO transactionReceivedDTO){
        Transaction transaction;
        Account accountResquetedRefund = null;
        Account accountRefunded = null;
        Wallet walletRequestedRefund;
        Wallet walletRefunded;
        try{
            accountResquetedRefund = validAccountAndWallet(transactionReceivedDTO.getCpf(), transactionReceivedDTO.getName());
            walletRequestedRefund = accountResquetedRefund.getWallet();

            Transaction transactionRefund = transactionRepository.findById(transactionReceivedDTO.getTransactionRefundId())
                    .orElseThrow(() -> new InvalidTransactionException("Invalid Transaction Refund ID"));

            accountRefunded = validAccountAndWallet(transactionRefund.getAccountAddressee().getCPF(), transactionRefund.getAccountAddressee().getName());
            walletRefunded = accountRefunded.getWallet();

            if(!isValidWalletKey(transactionReceivedDTO.getWalletKey(), walletRequestedRefund.getWalletKey()))
                throw new InvalidWalletKeyException();

            if(!passwordEncoder.matches(transactionReceivedDTO.getWalletPassword(), walletRequestedRefund.getPassword()))
                throw new BadCredentialsException("Invalid Wallet Password");

            if(!accountResquetedRefund.getId().equals(transactionRefund.getAccountOrigin().getId())) throw new EntityNotFoundException();

            if(!accountRefunded.getId().equals(transactionRefund.getAccountAddressee().getId())) throw new EntityNotFoundException();

            if(!transactionRefund.getTransactionStatus().equalsIgnoreCase(TransactionStatusEnum.COMPLETED.getValue()))
                throw new InvalidTransactionException("Transaction was not completed");

            if(transactionRefund.getTransactionStatus().equalsIgnoreCase(TransactionStatusEnum.REFUNDED.getValue()))
                throw new TransactionFailedException("Transaction is refunded");

            if(!transactionRefund.getTransactionType().equalsIgnoreCase(TransactionTypeEnum.TRANSFER.getValue()) &&
                    !transactionRefund.getTransactionType().equalsIgnoreCase(TransactionTypeEnum.PAYMENT.getValue()))
                throw  new InvalidTransactionException("Transaction type cannot be refunded");

            walletRefunded.setBalance(walletRefunded.getBalance() - transactionRefund.getAmount());
            walletRequestedRefund.setBalance(walletRequestedRefund.getBalance() + transactionRefund.getAmount());

            transaction = createTransactionSuccessful(accountRefunded, accountResquetedRefund, transactionReceivedDTO, TransactionTypeEnum.REFUND);
            transaction.setTransactionRefund(transactionRefund);
            transaction.setAmount(transactionRefund.getAmount());
            transactionRefund.setTransactionStatus(TransactionStatusEnum.REFUNDED);
            transactionRepository.save(transactionRefund);
            transactionRepository.save(transaction);

        }catch (Exception e){
            TransactionFailed transactionFailed = createTransactionFailed(accountRefunded, accountResquetedRefund, transactionReceivedDTO, e, TransactionTypeEnum.REFUND);
            transactionRepository.save(transactionFailed);
            throw e;
        }

        notificationService.createNotification("Reembolso realizado com sucesso!",
                "Parabéns, seu reembolso foi aprovado e em breve o saldo será adicionado na sua carteira",
                NotificationTypeEnum.TRANSACTION, accountResquetedRefund);
        notificationService.createNotification("Saldo reembolsado!", "O saldo de uma transação foi reembolsado da sua carteira",
                NotificationTypeEnum.TRANSACTION, accountRefunded);
        notificationService.notifyAccount(accountResquetedRefund.getId());
        notificationService.notifyAccount(accountRefunded.getId());

        return CustomMapperTransaction.mapperObject(transaction);
    }

    private TransactionDTO withdrawal(TransactionReceivedDTO transactionReceivedDTO){
        Transaction transaction;
        Account accountOrigin = validAccountAndWallet(transactionReceivedDTO.getCpf(),
                transactionReceivedDTO.getName());

        Wallet walletOrigin = accountOrigin.getWallet();
        try{

            if(!isValidWalletKey(transactionReceivedDTO.getWalletKey(), walletOrigin.getWalletKey()))
                throw new InvalidWalletKeyException();

            if(!passwordEncoder.matches(transactionReceivedDTO.getWalletPassword(), walletOrigin.getPassword()))
                throw new BadCredentialsException("Invalid Wallet Password");

            walletOrigin.setBalance(walletOrigin.getBalance() - transactionReceivedDTO.getAmount());

            transaction = createTransactionSuccessful(accountOrigin, transactionReceivedDTO, TransactionTypeEnum.WITHDRAWAL);
            transactionRepository.save(transaction);

        }catch (Exception e){
            TransactionFailed transactionFailed = createTransactionFailed(accountOrigin, transactionReceivedDTO, e, TransactionTypeEnum.WITHDRAWAL);
            transactionFailedRepository.save(transactionFailed);
            throw e;
        }

        String messageNotification = "Um Saque no valor de R$: " + transaction.getAmount() + " foi realizado";
        notificationService.createNotification("Saque realizado!", messageNotification,
                NotificationTypeEnum.TRANSACTION, accountOrigin);
        notificationService.notifyAccount(accountOrigin.getId());

        return CustomMapperTransaction.mapperObject(transaction);
    }

    private TransactionDTO deposit(TransactionReceivedDTO transactionReceivedDTO){
        Transaction transaction = new Transaction();
        Account accountOrigin = validAccountAndWallet(transactionReceivedDTO.getCpf(),
                transactionReceivedDTO.getName());
        Wallet walletOrigin = accountOrigin.getWallet();

        try{
            if(!isValidWalletKey(transactionReceivedDTO.getWalletKey(), walletOrigin.getWalletKey()))
                throw new InvalidWalletKeyException();

            if(!passwordEncoder.matches(transactionReceivedDTO.getWalletPassword(), walletOrigin.getPassword()))
                throw new BadCredentialsException("Invalid Wallet Password");

            walletOrigin.setBalance(walletOrigin.getBalance() + transactionReceivedDTO.getAmount());

            transaction = createTransactionSuccessful(accountOrigin, transactionReceivedDTO, TransactionTypeEnum.DEPOSIT);
            transactionRepository.save(transaction);

        }catch (Exception e){
            TransactionFailed transactionFailed = createTransactionFailed(accountOrigin, transactionReceivedDTO, e, TransactionTypeEnum.DEPOSIT);
            transactionFailedRepository.save(transactionFailed);
        }

        String messageNotification = "Um deposito no valor de R$: " + transaction.getAmount() + " foi adicionado na sua carteira";
        notificationService.createNotification("Deposito realizado!", messageNotification,
                NotificationTypeEnum.TRANSACTION, accountOrigin);
        notificationService.notifyAccount(accountOrigin.getId());

        return CustomMapperTransaction.mapperObject(transaction);
    }

    private TransactionDTO  transfer(TransactionReceivedDTO transactionReceivedDTO){
        Transaction transaction;
        Account accountAddressee = validAccountAndWallet(transactionReceivedDTO.getAccountAddresseeCPF(),
                transactionReceivedDTO.getAccountAddresseeName());
        Account accountOrigin = validAccountAndWallet(transactionReceivedDTO.getCpf(),
                transactionReceivedDTO.getName());
        Wallet walletOrigin = accountOrigin.getWallet();
        Wallet walletAddressee = accountAddressee.getWallet();

        try{
            if(!isValidWalletKey(transactionReceivedDTO.getWalletKey(), walletOrigin.getWalletKey()))
                throw new InvalidWalletKeyException();

            if(!passwordEncoder.matches(transactionReceivedDTO.getWalletPassword(), walletOrigin.getPassword()))
                throw new BadCredentialsException("Invalid Wallet Password");

            if(!(walletOrigin.getBalance() >= transactionReceivedDTO.getAmount()))
                throw new TransactionFailedException("Insufficient Funds");

            walletOrigin.setBalance(walletOrigin.getBalance() - transactionReceivedDTO.getAmount());
            walletAddressee.setBalance(walletAddressee.getBalance() + transactionReceivedDTO.getAmount());

           transaction = createTransactionSuccessful(accountOrigin, accountAddressee, transactionReceivedDTO, TransactionTypeEnum.TRANSFER);
           transactionRepository.save(transaction);

        }catch (Exception e){
          TransactionFailed transactionFailed = createTransactionFailed(accountOrigin, accountAddressee, transactionReceivedDTO, e, TransactionTypeEnum.TRANSFER);
          transactionRepository.save(transactionFailed);
            throw e;
        }

        String messageOrigin = "Você realizou uma transferencia no valor de R$: " + transaction.getAmount() + ", para " + accountAddressee.getName();

        notificationService.createNotification("Transferencia realizado com sucesso!",
                messageOrigin, NotificationTypeEnum.TRANSACTION, accountOrigin);
        notificationService.notifyAccount(accountOrigin.getId());

        String messageAddressee = accountOrigin.getName() + " enviou R$: " + transaction.getAmount() + " para sua carteira";

        notificationService.createNotification("Transferencia recebida!",
                messageAddressee, NotificationTypeEnum.TRANSACTION, accountAddressee);
        notificationService.notifyAccount(accountAddressee.getId());

        return CustomMapperTransaction.mapperObject(transaction);
    }

    private TransactionDTO charge(TransactionReceivedDTO transactionReceivedDTO){
        Transaction transaction;
        Account accountCharging = null;
        Account accountCharged = null;
        Wallet walletCharging;
        try {
            accountCharging = validAccountAndWallet(transactionReceivedDTO.getCpf(), transactionReceivedDTO.getName());
            walletCharging = accountCharging.getWallet();
            accountCharged = validAccountAndWallet(transactionReceivedDTO.getAccountAddresseeCPF(), transactionReceivedDTO.getAccountAddresseeName());

            if(!isValidWalletKey(transactionReceivedDTO.getWalletKey(), walletCharging.getWalletKey()))
                throw new InvalidWalletKeyException();

            if(!passwordEncoder.matches(transactionReceivedDTO.getWalletPassword(), walletCharging.getPassword()))
                throw new BadCredentialsException("Invalid Wallet Password");

            transaction = createTransactionSuccessful(accountCharging, accountCharged, transactionReceivedDTO, TransactionTypeEnum.CHARGE);
            transaction.setTransactionStatus(TransactionStatusEnum.PENDING);
            transaction.setIsPaymentDone(false);
            transactionRepository.save(transaction);

        }catch (Exception e){
            TransactionFailed transactionFailed = createTransactionFailed(accountCharging, accountCharged, transactionReceivedDTO, e, TransactionTypeEnum.CHARGE);
            transactionRepository.save(transactionFailed);
            throw e;
        }

        String messageOrigin = "Você realizou uma cobrança no valor de R$: " + transaction.getAmount() + ", para " + accountCharged.getName();

        notificationService.createNotification("Cobrança realizado com sucesso!",
                messageOrigin, NotificationTypeEnum.TRANSACTION, accountCharged);
        notificationService.notifyAccount(accountCharged.getId());

        String messageAddressee = "Você recebeu uma cobrança no valor de R$: " + transaction.getAmount() + ", enviada por " + accountCharging.getName();

        notificationService.createNotification("Cobrança recebida!",
                messageAddressee, NotificationTypeEnum.TRANSACTION, accountCharged);
        notificationService.notifyAccount(accountCharged.getId());

        return CustomMapperTransaction.mapperObject(transaction);
    }

    private TransactionDTO payment(TransactionReceivedDTO transactionReceivedDTO){
        Transaction transaction;
        Account accountCharging = null;
        Wallet walletCharging;
        Account accountCharged = validAccountAndWallet(transactionReceivedDTO.getCpf(), transactionReceivedDTO.getName());
        Wallet walletCharged = accountCharged.getWallet();

        try{
            if(!isValidWalletKey(transactionReceivedDTO.getWalletKey(), walletCharged.getWalletKey()))
                throw new InvalidWalletKeyException();

            if(!passwordEncoder.matches(transactionReceivedDTO.getWalletPassword(), walletCharged.getPassword()))
                throw new BadCredentialsException("Invalid Wallet Password");

            Transaction transactionCharge = transactionRepository.findById(transactionReceivedDTO
                    .getTransactionPaymentId()).orElseThrow(() -> new TransactionFailedException("Invalid Transaction Payment Id"));

            accountCharging = accountRepository.findById(transactionCharge.getAccountOrigin().getId())
                    .orElseThrow(EntityNotFoundException::new);
            walletCharging = accountCharging.getWallet();

            if(!(walletCharged.getBalance() >= transactionCharge.getAmount())) throw new TransactionFailedException("Insufficient Funds");
            if(!transactionCharge.getTransactionStatus().equalsIgnoreCase(TransactionStatusEnum.PENDING.getValue()))
                throw new TransactionFailedException("Charge already paid");

            walletCharged.setBalance(walletCharged.getBalance() - transactionCharge.getAmount());
            walletCharging.setBalance(walletCharging.getBalance() + transactionCharge.getAmount());

            transactionCharge.setTransactionStatus(TransactionStatusEnum.COMPLETED);
            transactionCharge.setIsPaymentDone(true);
            transactionCharge.setAccountOrigin(accountCharging);
            transactionCharge.setAccountAddressee(accountCharged);
            transaction = createTransactionSuccessful(accountCharged, accountCharging, transactionReceivedDTO, TransactionTypeEnum.PAYMENT);
            transactionRepository.save(transaction);
            transactionRepository.save(transactionCharge);

        }catch (Exception e){
            TransactionFailed transactionFailed = createTransactionFailed(accountCharged, accountCharging, transactionReceivedDTO, e, TransactionTypeEnum.PAYMENT);
            transactionFailedRepository.save(transactionFailed);
            throw e;
        }

        String messageOrigin = "Você realizou um pagamento no valor de R$: " + transaction.getAmount() + ", para " + accountCharged.getName();

        notificationService.createNotification("Pagamento realizado com sucesso!",
                messageOrigin, NotificationTypeEnum.TRANSACTION, accountCharged);
        notificationService.notifyAccount(accountCharged.getId());

        String messageAddressee = "Você recebeu uma cobrança no valor de R$: " + transaction.getAmount() + ", enviada por " + accountCharging.getName();

        notificationService.createNotification("Pagamento recebido!",
                messageAddressee, NotificationTypeEnum.TRANSACTION, accountCharged);
        notificationService.notifyAccount(accountCharged.getId());

        return CustomMapperTransaction.mapperObject(transaction);
    }

    private Transaction createTransactionSuccessful(Account accountOrigin, Account accountAddressee,
                                             TransactionReceivedDTO transactionReceivedDTO, TransactionTypeEnum transactionType){
        Transaction transaction = new Transaction();
        Date date = new Date();
        transaction.setTransactionStatus(TransactionStatusEnum.COMPLETED);
        transaction.setTransactionType(transactionType);
        transaction.setDescription(transactionReceivedDTO.getDescription());
        transaction.setAccountOrigin(accountOrigin);
        transaction.setAccountAddressee(accountAddressee);
        transaction.setAmount(transactionReceivedDTO.getAmount());
        transaction.setTransactionCreatedAt(date);
        return transaction;
    }

    private Transaction createTransactionSuccessful(Account accountOrigin, TransactionReceivedDTO transactionReceivedDTO, TransactionTypeEnum transactionType){
        Transaction transaction = new Transaction();
        Date date = new Date();
        transaction.setTransactionStatus(TransactionStatusEnum.COMPLETED);
        transaction.setTransactionType(transactionType);
        transaction.setDescription(transactionReceivedDTO.getDescription());
        transaction.setAccountOrigin(accountOrigin);
        transaction.setAccountAddressee(accountOrigin);
        transaction.setAmount(transactionReceivedDTO.getAmount());
        transaction.setTransactionCreatedAt(date);
        return transaction;
    }

    private TransactionFailed createTransactionFailed(Account accountOrigin, Account accountAddressee,
                                         TransactionReceivedDTO transactionReceivedDTO, Exception e, TransactionTypeEnum transactionType){
        Date date = new Date();
        TransactionFailed transactionFailed = new TransactionFailed();
        transactionFailed.setError(e.getMessage());
        transactionFailed.setExceptionThrown(e.getClass().toString());
        transactionFailed.setTimestamp(Instant.now());
        transactionFailed.setDescription(transactionReceivedDTO.getDescription());
        transactionFailed.setAccountOrigin(accountOrigin);
        transactionFailed.setTransactionType(transactionType);
        transactionFailed.setTransactionStatus(TransactionStatusEnum.FAILED);
        transactionFailed.setAmount(transactionReceivedDTO.getAmount());
        transactionFailed.setTransactionCreatedAt(date);

        transactionFailed.setAccountAddressee(accountAddressee);

        return transactionFailed;

    }

    private TransactionFailed createTransactionFailed(Account accountOrigin,
                                                      TransactionReceivedDTO transactionReceivedDTO, Exception e, TransactionTypeEnum transactionType){
        Date date = new Date();
        TransactionFailed transactionFailed = new TransactionFailed();
        transactionFailed.setError(e.getMessage());
        transactionFailed.setExceptionThrown(e.getClass().toString());
        transactionFailed.setTimestamp(Instant.now());
        transactionFailed.setDescription(transactionReceivedDTO.getDescription());
        transactionFailed.setAccountOrigin(accountOrigin);
        transactionFailed.setTransactionType(transactionType);
        transactionFailed.setTransactionStatus(TransactionStatusEnum.FAILED);
        transactionFailed.setAmount(transactionReceivedDTO.getAmount());
        transactionFailed.setTransactionCreatedAt(date);
        return transactionFailed;

    }

    private Account validAccountAndWallet(String accountCPF, String accountName){
        Account account = accountRepository.findByCPF(accountCPF);
        if(account == null || !account.getName().equalsIgnoreCase(accountName)) throw new EntityNotFoundException();
        if(!account.getEnabled()) throw new AccountIsNotActiveException();
        if(!account.getAccountNonExpired()) throw new InvalidAccountException("Account Expired");
        if(!account.getAccountNonLocked()) throw new InvalidAccountException("Account Locked");
        Wallet wallet = account.getWallet();
        if(!wallet.getEnabled()) throw new WalletIsNotActiveException();
        if(!wallet.getWalletNonLocked()) throw new InvalidAccountException("Wallet Locked");
        return account;
    }


    private Boolean isValidWalletKey(String encryptedWalletKeyData, String accountWalletKey){
        String walletKey = crypto.decrypt(encryptedWalletKeyData);
        return walletKey.equalsIgnoreCase(accountWalletKey);
    }
}
