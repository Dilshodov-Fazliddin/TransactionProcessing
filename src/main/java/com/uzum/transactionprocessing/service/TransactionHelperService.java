package com.uzum.transactionprocessing.service;

import com.uzum.transactionprocessing.entity.TransactionEntity;

public interface TransactionHelperService {
    TransactionEntity saveTransaction(TransactionEntity entity);
}
