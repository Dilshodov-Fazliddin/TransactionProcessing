package com.uzum.transactionprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TransactionProcessingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionProcessingApplication.class, args);
    }

}
