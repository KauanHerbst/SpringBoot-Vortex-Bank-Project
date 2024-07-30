package com.herbst.vortexbank.repositories;

import com.herbst.vortexbank.entities.TransactionFailed;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TransactionFailedRepository extends JpaRepository<TransactionFailed, String> {
}
