package com.herbst.vortexbank.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tb_wallets")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Wallet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double balance;
    private String password;
    private Boolean enabled;
    @Column(name = "wallet_key")
    private String walletKey;
    @Column(name = "type_of_currency")
    private String typeOfCurrency;
    @Column(name = "wallet_non_locked")
    private Boolean walletNonLocked;
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "wallet_created_at")
    private Date walletCreatedAt;

    @OneToOne(mappedBy = "wallet")
    private Account account;

}
