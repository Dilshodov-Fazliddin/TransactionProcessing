package com.uzum.transactionprocessing.dto.response;

import com.uzum.transactionprocessing.constant.enums.Currency;

import java.time.LocalDate;

public record CmsResponse(Long id, Long accountId, AccountStatus accountStatus, Currency currency, LocalDate cardExpireDate) {
}
