package com.uzum.transactionprocessing.constant;


import org.springframework.beans.factory.annotation.Value;

public class Constants {
    public final static int feeAmount = 1;

    @Value(value = "${url.cms}")
    public static String cmsUrl;

    @Value(value = "${url.coreLedger}")
    public static String coreLedgerUrl;

}
