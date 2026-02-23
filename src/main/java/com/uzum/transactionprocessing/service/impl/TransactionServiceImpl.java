package com.uzum.transactionprocessing.service.impl;

import com.uzum.transactionprocessing.component.kafka.producer.TransactionEvenProducer;
import com.uzum.transactionprocessing.constant.enums.Error;
import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import com.uzum.transactionprocessing.dto.event.TransactionValidateEvent;
import com.uzum.transactionprocessing.dto.request.TransactionRequest;
import com.uzum.transactionprocessing.dto.response.TransactionResponse;
import com.uzum.transactionprocessing.entity.TransactionEntity;
import com.uzum.transactionprocessing.exception.ReferenceIdExistsException;
import com.uzum.transactionprocessing.exception.kafka.nontransients.TransactionInvalidException;
import com.uzum.transactionprocessing.mapper.TransactionMapper;
import com.uzum.transactionprocessing.repository.TransactionRepository;
import com.uzum.transactionprocessing.service.TransactionHelperService;
import com.uzum.transactionprocessing.service.TransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TransactionServiceImpl implements TransactionService {
    TransactionRepository transactionRepository;
    TransactionMapper transactionMapper;
    TransactionEvenProducer transactionEvenProducer;
    TransactionHelperService transactionHelperService;

    @Transactional
    public void changeTransactionStatus(final Long transactionId, final TransactionStatus status) {
        transactionRepository.updateStatus(transactionId, status);
    }

    @Transactional
    public void updateFee(Long transactionId, Long fee) {
        transactionRepository.updateFee(transactionId, fee);
    }

    @Transactional(readOnly = true)
    public TransactionEntity findById(final Long transactionId) throws TransactionInvalidException {
        return transactionRepository.findById(transactionId).orElseThrow(() -> new TransactionInvalidException(Error.TRANSACTION_ID_INVALID_CODE));
    }

    public TransactionResponse createTransaction(TransactionRequest request) {
        validateReferenceId(request);

        TransactionEntity transactionEntity = transactionMapper.toEntity(request);

        TransactionEntity savedTransaction = transactionHelperService.saveTransaction(transactionEntity);

        TransactionValidateEvent event = TransactionValidateEvent.of(savedTransaction.getId());
        transactionEvenProducer.publishForSenderValidation(event);

        return transactionMapper.toResponse(transactionRepository.save(transactionEntity));
    }

    @Override
    @Transactional
    public void storeSenderAccountId(Long transactionId, UUID senderAccountId) {
        transactionRepository.updateSenderAccountIdAndStatus(transactionId, senderAccountId);
    }

    @Override
    @Transactional
    public void storeReceiverAccountId(Long transactionId, UUID receiverAccountId) {
        transactionRepository.updateReceiverAccountIdAndStatus(transactionId, receiverAccountId);
    }

    private void validateReferenceId(TransactionRequest request) {
        boolean referenceIdExists = transactionRepository.existsByReferenceId(request.referenceId());

        if (referenceIdExists) {
            throw new ReferenceIdExistsException(Error.REFERENCE_ID_EXISTS_CODE);
        }
    }
}
