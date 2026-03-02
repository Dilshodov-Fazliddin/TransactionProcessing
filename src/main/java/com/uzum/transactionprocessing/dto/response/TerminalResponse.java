package com.uzum.transactionprocessing.dto.response;

import java.util.UUID;

public record TerminalResponse(
        Long id,

        String webhookUrl,

        String terminalStatus,

        String terminalNumber
) {
}
