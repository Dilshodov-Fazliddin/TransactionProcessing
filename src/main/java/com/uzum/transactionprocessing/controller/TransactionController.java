package com.uzum.transactionprocessing.controller;

import com.uzum.transactionprocessing.dto.request.TransactionRequest;
import com.uzum.transactionprocessing.dto.response.TransactionResponse;
import com.uzum.transactionprocessing.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionRequest transactionRequest) {
        TransactionResponse response = transactionService.saveTransaction(transactionRequest);
        return ResponseEntity.ok(response);
    }

}
