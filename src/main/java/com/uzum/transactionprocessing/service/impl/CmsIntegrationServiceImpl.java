package com.uzum.transactionprocessing.service.impl;

import com.uzum.transactionprocessing.constant.enums.Currency;
import com.uzum.transactionprocessing.dto.response.AccountStatus;
import com.uzum.transactionprocessing.dto.response.CmsResponse;
import com.uzum.transactionprocessing.entity.TransactionEntity;
import com.uzum.transactionprocessing.exception.kafka.nontransients.CredentialsInvalidException;
import com.uzum.transactionprocessing.service.CmsIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CmsIntegrationServiceImpl implements CmsIntegrationService {
    @Override
    public CmsResponse fetchCardInfoByToken(String token) {
        // send http request to CMS service
        return null;
    }

    @Override
    public void validateCardToken(final String token, final Currency currency) {
        CmsResponse cmsResponse = fetchCardInfoByToken(token);

        if (cmsResponse.cardExpireDate().isBefore(LocalDate.now())) {
            throw new CredentialsInvalidException(com.uzum.transactionprocessing.constant.enums.Error.CARD_EXPIRED_CODE);
        }

        if (!cmsResponse.accountStatus().equals(AccountStatus.ACTIVE)) {
            throw new CredentialsInvalidException(com.uzum.transactionprocessing.constant.enums.Error.ACCOUNT_NOT_ACTIVE_CODE);
        }

        if (!cmsResponse.currency().equals(currency)) {
            throw new CredentialsInvalidException(com.uzum.transactionprocessing.constant.enums.Error.CURRENCY_INVALID_CODE);
        }

    }
}
