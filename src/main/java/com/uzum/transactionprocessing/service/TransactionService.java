package com.uzum.transactionprocessing.service;

import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import com.uzum.transactionprocessing.dto.request.TransactionRequest;
import com.uzum.transactionprocessing.dto.response.TransactionResponse;
import com.uzum.transactionprocessing.entity.TransactionEntity;
import com.uzum.transactionprocessing.exception.kafka.nontransiets.TransactionInvalidException;

public interface TransactionService {
    void changeTransactionStatusAndUnclaim(Long transactionId, TransactionStatus status);

    int claimForProcessing(Long transactionId);

    void unclaim(Long transactionId);

    void updateFee(Long transactionId, Long fee);

    TransactionEntity findById(final Long transactionId) throws TransactionInvalidException;

    TransactionResponse saveTransaction(final TransactionRequest request) throws TransactionInvalidException;
}
