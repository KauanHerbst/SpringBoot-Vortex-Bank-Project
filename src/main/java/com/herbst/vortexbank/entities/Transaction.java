package com.herbst.vortexbank.entities;

import com.herbst.vortexbank.util.TransactionStatusEnum;
import com.herbst.vortexbank.util.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "tb_transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Transaction implements Serializable {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(length = 36)
    private String id;
    @Column(name = "transaction_type")
    private String transactionType;
    @Column(name = "transaction_status")
    private String transactionStatus;
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "transaction_created_at")
    private Date transactionCreatedAt;
    private String description;
    private Double amount;
    @Column(name = "is_payment_done")
    private Boolean isPaymentDone;

    @ManyToOne
    @JoinColumn(name = "transaction_refund_id")
    private Transaction transactionRefund;

    @ManyToOne
    @JoinColumn(name = "account_addressee_id")
    private Account accountAddressee;

    @ManyToOne
    @JoinColumn(name = "account_origin_id")
    private Account accountOrigin;

    public void setTransactionType(TransactionTypeEnum transactionType){
        this.transactionType = transactionType.getValue();
    }

    public void setTransactionStatus(TransactionStatusEnum transactionStatus){
        this.transactionStatus = transactionStatus.getValue();
    }

}
