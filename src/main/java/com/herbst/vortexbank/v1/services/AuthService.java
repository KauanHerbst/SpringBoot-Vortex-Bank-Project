package com.herbst.vortexbank.v1.services;

import com.herbst.vortexbank.entities.Account;
import com.herbst.vortexbank.entities.Permission;
import com.herbst.vortexbank.exceptions.AccountAlreadyCreatedWithCPFException;
import com.herbst.vortexbank.exceptions.AccountAlreadyCreatedWithEmailException;
import com.herbst.vortexbank.exceptions.EntityNotFoundException;
import com.herbst.vortexbank.mapper.MapperObject;
import com.herbst.vortexbank.repositories.AccountRepository;
import com.herbst.vortexbank.repositories.PermissionReporitory;
import com.herbst.vortexbank.security.jwt.TokenProvider;
import com.herbst.vortexbank.v1.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private PermissionReporitory permissionReporitory;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AccountDTO create(CreateAccountDTO accountDataDTO) {
        if (accountRepository.existsByEmail(accountDataDTO.getEmail()))
            throw new AccountAlreadyCreatedWithEmailException();
        if (accountRepository.existsByCPF(accountDataDTO.getCPF())) throw new AccountAlreadyCreatedWithCPFException();
        Account accountData = MapperObject.objectValue(accountDataDTO, Account.class);
        accountData.setAccountNonLocked(true);
        accountData.setAccountNonExpired(true);
        accountData.setCredentialsNonExpired(true);
        accountData.setEnabled(false);
        Date now = new Date();
        accountData.setAccountCreatedAt(now);
        Optional<Permission> permissionOptional = permissionReporitory.findById(1L);
        accountData.getPermissions().add(permissionOptional.get());
        accountRepository.save(accountData);
        return MapperObject.objectValue(accountData, AccountDTO.class);
    }

    public TokenDTO signin(AccountSignInDTO accountSignInDTO){
        if(!accountRepository.existsByCPF(accountSignInDTO.getCPF())) throw new EntityNotFoundException();
        try{
            String CPF = accountSignInDTO.getCPF();
            String password = accountSignInDTO.getPassword();
            Account account = accountRepository.findByCPF(CPF);
            String name = account.getName();
            String accountPassword = account.getPassword();
            String email = account.getEmail();
            List<String> permissions = account.getPermissionsAccount();
            if(!passwordEncoder.matches(password, accountPassword)) throw new BadCredentialsException("Invalid Password");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(name, password));
            return tokenProvider.createAccessToken(name, CPF, email, permissions);
        }catch (Exception e){
            throw new BadCredentialsException("Invalid Credentials");
        }
    }

    public TokenDTO refreshToken(RefreshTokenDTO refreshTokenDTO){
        String CPF = refreshTokenDTO.getCPF();
        String refreshToken = refreshTokenDTO.getRefreshToken();
        Boolean accountIsExists = accountRepository.existsByCPF(CPF);
        if(!accountIsExists) throw new EntityNotFoundException();
        return tokenProvider.refreshToken(refreshToken);
    }
}
