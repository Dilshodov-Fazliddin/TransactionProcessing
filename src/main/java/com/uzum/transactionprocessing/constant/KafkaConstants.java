package com.uzum.transactionprocessing.constant;

public class KafkaConstants {
    //@Dadamuhames
    public static final String SENDER_VALIDATE_TOPIC = "transactions.validate.sender";
    public static final String SENDER_VALIDATE_GROUP_ID = "transactions.validate.sender.group";


    //@Dadamuhames
    public static final String RECEIVER_VALIDATE_TOPIC = "transactions.validate.receiver";
    public static final String RECEIVER_VALIDATE_GROUP_ID = "transactions.validate.receiver.group";


    //@Dilshodov-Fazliddin
    public static final String AMOUNT_VALIDATE_TOPIC = "transactions.validate.amount";
    public static final String AMOUNT_VALIDATE_GROUP_ID = "transactions.validate.amount.group";

    //@Dilshodov-Fazliddin
    public static final String CALCULATE_FEE = "transactions.fee.calculate";
    public static final String CALCULATE_FEE_GROUP_ID = "transactions.fee.calculate.group";


    public static final String TRANSACTIONS_TOPIC = "transaction.transactions.topic";

    public static final String TRANSACTIONS_GROUP_ID = "transaction.transactions.group";

    public static final String TRUSTED_PACKAGE = "com.uzum.transactionprocessing.dto.event";
}
