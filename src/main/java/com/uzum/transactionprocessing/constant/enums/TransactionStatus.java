package com.uzum.transactionprocessing.constant.enums;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    CREATED(false),
    SENDER_INFO_VALIDATED(false),
    SENDER_INFO_VALIDATION_FAILED(true),
    RECEIVER_INFO_VALIDATED(false),
    RECEIVER_INFO_VALIDATION_FAILED(true),
    AMOUNT_VALIDATED(false),
    AMOUNT_VALIDATION_FAILED(true),
    CALCULATE_FAILED(true),
    SENT_TO_CORE_LEDGER(true),
    SUCCESS(true),
    FAILED(true);

    final boolean isTerminal;

    TransactionStatus(boolean isTerminal) {this.isTerminal = isTerminal;}
}
