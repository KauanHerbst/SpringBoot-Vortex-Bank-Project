package com.herbst.vortexbank.repositories;

import com.herbst.vortexbank.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
