package com.uzum.transactionprocessing.mapper;

import com.uzum.transactionprocessing.dto.event.TransactionLedgerEvent;
import com.uzum.transactionprocessing.dto.request.TransactionRequest;
import com.uzum.transactionprocessing.dto.response.TransactionResponse;
import com.uzum.transactionprocessing.entity.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "fee", constant = "0L")
    TransactionEntity toEntity(TransactionRequest transactionRequest);

    TransactionResponse toResponse(TransactionEntity transactionEntity);

    @Mapping(target = "transactionId", source = "entity.id")
    TransactionLedgerEvent entityToLedgerEvent(TransactionEntity entity);
}