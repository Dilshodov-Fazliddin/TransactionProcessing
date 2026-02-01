package com.uzum.transactionprocessing.service;

import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import com.uzum.transactionprocessing.entity.TransactionEntity;
import com.uzum.transactionprocessing.exception.kafka.nontransients.TransactionInvalidException;

public interface TransactionService {
    void changeTransactionStatusAndUnclaim(Long transactionId, TransactionStatus status);

    int claimForProcessing(Long transactionId);

    void unclaim(Long transactionId);

    TransactionEntity findById(final Long transactionId) throws TransactionInvalidException;
}
