package com.uzum.transactionprocessing.service.impl;

import com.uzum.transactionprocessing.constant.enums.AccountStatus;
import com.uzum.transactionprocessing.constant.enums.Currency;
import com.uzum.transactionprocessing.exception.kafka.nontransiets.CredentialsInvalidException;
import com.uzum.transactionprocessing.service.CmsIntegrationService;
import com.uzum.transactionprocessing.service.SenderValidationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import static com.uzum.transactionprocessing.constant.enums.Error.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class SenderValidationServiceImpl implements SenderValidationService {

    CmsIntegrationService cmsIntegrationService;
    CoreLedgerIntegrationServiceImpl coreLedgerIntegrationService;

    @Override
    public void validateAmount(String senderToken, Long amount, Currency currency) {

        var cmsResponse = cmsIntegrationService.fetchCardInfoByToken(senderToken);
        var coreLedgerResponse = coreLedgerIntegrationService.fetchBalanceByAccountId(cmsResponse.accountId());

        if (coreLedgerResponse.accountStatus().equals(AccountStatus.ACTIVE)) {

            if (!coreLedgerResponse.currency().equals(currency)) {
                throw new CredentialsInvalidException(CURRENCY_INVALID_CODE);
            }

            if (!(coreLedgerResponse.currentBalance() >= amount)) {
                throw new CredentialsInvalidException(AMOUNT_NOT_ENOUGH);

            }
        } else {
            throw new CredentialsInvalidException(INVALID_ACCOUNT_STATUS);
        }
    }

}
