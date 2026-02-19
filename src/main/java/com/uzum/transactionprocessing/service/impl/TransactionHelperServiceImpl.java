package com.uzum.transactionprocessing.service.impl;

import com.uzum.transactionprocessing.entity.TransactionEntity;
import com.uzum.transactionprocessing.repository.TransactionRepository;
import com.uzum.transactionprocessing.service.TransactionHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TransactionHelperServiceImpl implements TransactionHelperService {

    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionEntity saveTransaction(TransactionEntity entity) {
        return transactionRepository.save(entity);
    }
}
