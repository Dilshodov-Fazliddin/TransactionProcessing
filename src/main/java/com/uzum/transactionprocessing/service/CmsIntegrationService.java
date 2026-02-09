package com.uzum.transactionprocessing.service;

import com.uzum.transactionprocessing.constant.enums.Currency;
import com.uzum.transactionprocessing.dto.response.CmsResponse;

public interface CmsIntegrationService {
    void validateCardToken(String token, Currency currency);

}
