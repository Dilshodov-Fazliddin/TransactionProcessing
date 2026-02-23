package com.uzum.transactionprocessing.entity;

import com.uzum.transactionprocessing.constant.enums.Currency;
import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import com.uzum.transactionprocessing.constant.enums.TransactionType;
import com.uzum.transactionprocessing.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "transactions")
public class TransactionEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    UUID referenceId;

    @Column(nullable = false)
    Long merchantId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "type", nullable = false)
    TransactionType type;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "status", nullable = false)
    TransactionStatus status;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "currency", nullable = false)
    Currency currency;

    @Column(nullable = false)
    @Positive
    Long amount;

    @Column(nullable = false)
    @PositiveOrZero
    Long fee;

    UUID senderAccountId;

    @Column(nullable = false)
    String senderName;

    @Column(nullable = false)
    String senderToken;

    UUID receiverAccountId;

    @Column(nullable = false)
    String receiverName;

    @Column(nullable = false)
    String receiverToken;
}
