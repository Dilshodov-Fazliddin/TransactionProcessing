package com.uzum.transactionprocessing.repository;

import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import com.uzum.transactionprocessing.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    @Modifying
    @Query("update TransactionEntity t set t.status = :status, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :transactionId ")
    void updateStatus(@Param("transactionId") Long transactionId, @Param("status") TransactionStatus status);

    @Modifying
    @Query("update TransactionEntity t SET t.fee = :fee  WHERE t.id = :transactionId")
    void updateFee(Long transactionId, Long fee);

    boolean existsByReferenceId(UUID referenceId);
}
