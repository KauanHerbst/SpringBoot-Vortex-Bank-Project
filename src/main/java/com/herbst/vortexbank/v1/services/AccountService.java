package com.herbst.vortexbank.v1.services;

import com.herbst.vortexbank.entities.Account;
import com.herbst.vortexbank.exceptions.*;
import com.herbst.vortexbank.mapper.MapperObject;
import com.herbst.vortexbank.repositories.AccountRepository;
import com.herbst.vortexbank.v1.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByName(username);
        if(account == null) throw new UsernameNotFoundException("Username Not Found");
        return account;
    }

    public UserDetails loadUserByNameAndCPFAndEmail(String name, String CPF, String email) throws  EntityNotFoundException{
        Account account = accountRepository.findByNameAndEmailAndCPF(name, email, CPF);
        if(account == null) throw new EntityNotFoundException();
        return account;
    }

    public StandardResponseDTO createAccountPassword(CreateAccountPasswordDTO accountPasswordDTO){
        if (!accountRepository.existsByNameAndEmailAndCPF(accountPasswordDTO.getName(), accountPasswordDTO.getEmail(), accountPasswordDTO.getCPF())) {
            throw new EntityNotFoundException();
        }
        Account account = accountRepository.findByCPF(accountPasswordDTO.getCPF());
        if(account.getEnabled()) throw new AccountIsActiveException();
        account.setPassword(passwordEncoder.encode(accountPasswordDTO.getPassword()));
        account.setEnabled(true);
        accountRepository.save(account);

        HttpStatus status = HttpStatus.ACCEPTED;
        String messageResponse = "Password Created";
        return new StandardResponseDTO(messageResponse, status.value());
    }

    public StandardResponseDTO changeAccountPassword(ChangeAccountPasswordDTO changeAccountPasswordDTO){
        if(!accountRepository.existsByNameAndEmailAndCPF(changeAccountPasswordDTO.getName(), changeAccountPasswordDTO.getEmail(),
                changeAccountPasswordDTO.getCPF())) throw new EntityNotFoundException();
        Account account = accountRepository.findByCPF(changeAccountPasswordDTO.getCPF());
        if(passwordEncoder.matches(changeAccountPasswordDTO.getPassword(), account.getPassword())){
            account.setPassword(passwordEncoder.encode(changeAccountPasswordDTO.getNewPassword()));
            accountRepository.save(account);

            HttpStatus status = HttpStatus.ACCEPTED;
            String messageResponse = "Password Changed";
            return new StandardResponseDTO(messageResponse, status.value());
        }
        HttpStatus status = HttpStatus.NOT_FOUND;
        String messageResponse = "Password not Changed";
        return new StandardResponseDTO(messageResponse, status.value());
    }

    public AccountTransactionDTO searchAccountForTransaction(AccountAddresseeDTO data){
        Account account = null;
        String keySearch = data.getKeySearch();
        if(isValidEmail(keySearch)){
            account = accountRepository.findByEmail(keySearch);
        }
        if(isValidCPF(keySearch)){
            account = accountRepository.findByCPF(keySearch);
        }
        if(isValidTelephone(keySearch)){
            account = accountRepository.findByTelephone(keySearch);
        }

        AccountTransactionDTO dto = new AccountTransactionDTO();

       if(account != null){
           dto.setName(account.getName());
           dto.setAccountId(account.getId());
       } else {
           throw new EntityNotFoundException();
       }
       return dto;
    }

    public AccountWithoutDetailsDTO getAccount(Long id){
        Optional<Account> accountOptional = accountRepository.findById(id);
        Account account = accountOptional.orElseThrow(() -> {
            throw  new EntityNotFoundException();
        });
        AccountWithoutDetailsDTO dto = new AccountWithoutDetailsDTO();
        dto.setAccountId(account.getId());
        dto.setCPF(account.getCPF());
        dto.setEmail(account.getEmail());
        dto.setName(account.getName());
        dto.setWalletKey(account.getWalletKey());
        return dto;
    }

    private Boolean isValidEmail(String email){
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private Boolean isValidCPF(String cpf){
        String cpfRegex = "^\\d{11}$";
        Pattern pattern = Pattern.compile(cpfRegex);
        Matcher matcher = pattern.matcher(cpf);
        return matcher.matches();
    }

    private Boolean isValidTelephone(String telephone){
        String telephoneRegex = "^\\d{9}$";
        Pattern pattern = Pattern.compile(telephoneRegex);
        Matcher matcher = pattern.matcher(telephone);
        return matcher.matches();
    }



}