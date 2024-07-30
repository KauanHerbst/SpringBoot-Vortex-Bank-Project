package com.herbst.vortexbank.repositories;

import com.herbst.vortexbank.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
