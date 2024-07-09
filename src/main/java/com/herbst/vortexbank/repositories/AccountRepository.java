package com.herbst.vortexbank.repositories;

import com.herbst.vortexbank.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByCPF(String CPF);
    Account findByNameAndEmailAndCPF(String name, String email, String CPF);
    Account findByName(String name);
    Boolean existsByNameAndEmailAndCPF(String name, String email, String CPF);
    Boolean existsByEmail(String email);
    Boolean existsByCPF(String CPF);

}
