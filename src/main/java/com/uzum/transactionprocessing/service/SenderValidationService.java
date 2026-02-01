package com.uzum.transactionprocessing.service;

import com.uzum.transactionprocessing.constant.enums.Currency;

public interface SenderValidationService {
    void validateAmount(String senderToken, Long amount, Currency currency);

}
