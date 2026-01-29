package com.uzum.transactionprocessing.repository;

import com.uzum.transactionprocessing.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity,Long> {
}
