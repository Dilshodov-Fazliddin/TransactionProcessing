package com.uzum.transactionprocessing.service.impl;

import com.uzum.transactionprocessing.constant.enums.Error;
import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import com.uzum.transactionprocessing.dto.request.TransactionRequest;
import com.uzum.transactionprocessing.dto.response.TransactionResponse;
import com.uzum.transactionprocessing.entity.TransactionEntity;
import com.uzum.transactionprocessing.exception.kafka.nontransiets.TransactionInvalidException;
import com.uzum.transactionprocessing.mapper.TransactionMapper;
import com.uzum.transactionprocessing.repository.TransactionRepository;
import com.uzum.transactionprocessing.service.TransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.cfg.MapperBuilder;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TransactionServiceImpl implements TransactionService {
    TransactionRepository transactionRepository;
    TransactionMapper transactionMapper;
    private final MapperBuilder mapperBuilder;

    @Transactional
    public void changeTransactionStatusAndUnclaim(final Long transactionId, final TransactionStatus status) {
        transactionRepository.updateStatusAndUnclaim(transactionId, status);
    }

    @Transactional
    public int claimForProcessing(Long transactionId) {
        return transactionRepository.claimForProcessing(transactionId);
    }

    @Transactional
    public void unclaim(Long transactionId) {
        transactionRepository.unclaim(transactionId);
    }

    @Transactional(readOnly = true)
    public TransactionEntity findById(final Long transactionId) throws TransactionInvalidException {
        return transactionRepository.findById(transactionId).orElseThrow(() -> new TransactionInvalidException(Error.TRANSACTION_ID_INVALID_CODE));
    }

    @Transactional
    public TransactionResponse saveTransaction(TransactionRequest request){
        TransactionEntity transactionEntity = transactionMapper.toEntity(request);
        return transactionMapper.toResponse(
                transactionRepository.save(transactionEntity));
    }
}
