package com.herbst.vortexbank.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Entity
@Table(name = "tb_transactions_failed")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class TransactionFailed extends Transaction {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(length = 36)
    private String id;
    private String error;
    @Column(name = "exception_thrown")
    private String exceptionThrown;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "error_timestamp")
    private Instant timestamp;
}
