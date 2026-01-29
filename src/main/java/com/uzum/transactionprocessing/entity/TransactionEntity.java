package com.uzum.transactionprocessing.entity;

import com.uzum.transactionprocessing.constant.enums.TransactionCurrency;
import com.uzum.transactionprocessing.constant.enums.TransactionState;
import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false , unique = true)
    Long referenceId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    TransactionStatus transactionStatus;

    @Column(nullable = false)
    Long amount;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    TransactionCurrency transactionCurrency;

    @Column(nullable = false)
    Long fee;

    @Column(nullable = false)
    String senderName;

    @Column(nullable = false)
    String senderToken;

    @Column(nullable = false)
    String receiverName;

    @Column(nullable = false)
    String receiverToken;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    TransactionState transactionState;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
