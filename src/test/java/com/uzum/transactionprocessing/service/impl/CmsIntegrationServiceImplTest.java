package com.uzum.transactionprocessing.service.impl;

import com.uzum.transactionprocessing.constant.enums.Currency;
import com.uzum.transactionprocessing.constant.enums.AccountStatus;
import com.uzum.transactionprocessing.dto.response.CmsResponse;
import com.uzum.transactionprocessing.exception.kafka.nontransiets.CredentialsInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CmsIntegrationServiceImplTest {

    private final String TOKEN = "token";

    private final String INVALID_TOKEN = "token_invalid";

    @Spy
    private CmsIntegrationServiceImpl cmsService;

    private CmsResponse cmsResponse;

    @BeforeEach
    void setUp() {
        cmsResponse = new CmsResponse(1L, 1L, AccountStatus.ACTIVE, Currency.UZS, LocalDate.now().plusYears(1));
    }

    @Test
    void validateCardToken_WhenValid_ShouldPass() {
        doReturn(cmsResponse).when(cmsService).fetchCardInfoByToken(TOKEN);

        when(cmsService.fetchCardInfoByToken(TOKEN)).thenReturn(cmsResponse);

        assertDoesNotThrow(() ->
            cmsService.validateCardToken(TOKEN, Currency.UZS)
        );
    }

    @Test
    void validateCardToke_WhenCmsAccountInactive_ShouldThrowException() {
        CmsResponse cmsResponse = new CmsResponse(1L, 1L, AccountStatus.INACTIVE, Currency.UZS, LocalDate.now().plusYears(1));

        doReturn(cmsResponse).when(cmsService).fetchCardInfoByToken(TOKEN);

        assertThrows(CredentialsInvalidException.class, () ->
            cmsService.validateCardToken(TOKEN, Currency.UZS)
        );
    }


    @Test
    void validateCardToken_WhenCurrencyMismatch_ShouldThrow() {
        doReturn(cmsResponse).when(cmsService).fetchCardInfoByToken(TOKEN);

        assertThrows(CredentialsInvalidException.class, () ->
            cmsService.validateCardToken(TOKEN, Currency.USD)
        );
    }

}