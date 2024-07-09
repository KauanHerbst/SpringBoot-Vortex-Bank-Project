package com.herbst.vortexbank.v1.services;

import com.herbst.vortexbank.entities.Account;
import com.herbst.vortexbank.exceptions.AccountAlreadyCreatedWithCPFException;
import com.herbst.vortexbank.exceptions.AccountAlreadyCreatedWithEmailException;
import com.herbst.vortexbank.exceptions.AccountIsActiveException;
import com.herbst.vortexbank.exceptions.EntityNotFoundException;
import com.herbst.vortexbank.mapper.MapperObject;
import com.herbst.vortexbank.repositories.AccountRepository;
import com.herbst.vortexbank.v1.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public Boolean createAccountPassword(CreateAccountPasswordDTO accountPasswordDTO){
        if(!accountRepository.existsByNameAndEmailAndCPF(accountPasswordDTO.getName(), accountPasswordDTO.getEmail(),
                accountPasswordDTO.getCPF())) throw new EntityNotFoundException();
        Account account = accountRepository.findByCPF(accountPasswordDTO.getCPF());
        if(account.getEnabled()) throw new AccountIsActiveException();
        account.setPassword(passwordEncoder.encode(accountPasswordDTO.getPassword()));
        account.setEnabled(true);
        accountRepository.save(account);
        return true;
    }

    public Boolean changeAccountPassword(ChangeAccountPasswordDTO changeAccountPasswordDTO){
        if(!accountRepository.existsByNameAndEmailAndCPF(changeAccountPasswordDTO.getName(), changeAccountPasswordDTO.getEmail(),
                changeAccountPasswordDTO.getCPF())) throw new EntityNotFoundException();
        Account account = accountRepository.findByCPF(changeAccountPasswordDTO.getCPF());
        if(passwordEncoder.matches(changeAccountPasswordDTO.getPassword(), account.getPassword())){
            account.setPassword(passwordEncoder.encode(changeAccountPasswordDTO.getNewPassword()));
            accountRepository.save(account);
            return true;
        }
        return false;
    }



}