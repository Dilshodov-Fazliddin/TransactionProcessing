package com.uzum.transactionprocessing.service;

import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import com.uzum.transactionprocessing.dto.request.TransactionRequest;
import com.uzum.transactionprocessing.dto.response.TransactionResponse;
import com.uzum.transactionprocessing.entity.TransactionEntity;
import com.uzum.transactionprocessing.exception.kafka.nontransients.TransactionInvalidException;

public interface TransactionService {
    void changeTransactionStatus(Long transactionId, TransactionStatus status);

    void updateFee(Long transactionId, Long fee);

    TransactionEntity findById(final Long transactionId) throws TransactionInvalidException;

    TransactionResponse createTransaction(final TransactionRequest request) throws TransactionInvalidException;
}
