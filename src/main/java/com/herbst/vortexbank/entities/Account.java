package com.herbst.vortexbank.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "tb_accounts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Account implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String CPF;
    private String email;
    private String password;
    private String telephone;
    @Column(name = "date_of_birth")
    private String dateOfBirth;
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "account_created_at")
    private Date accountCreatedAt;
    @Column(name = "account_non_expired")
    private Boolean accountNonExpired;
    @Column(name = "account_non_locked")
    private Boolean accountNonLocked;
    @Column(name = "credentials_non_expired")
    private Boolean credentialsNonExpired;
    @Column(name = "enabled")
    private Boolean enabled;
    @Column(name = "wallet_key")
    private String walletKey;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "tb_account_wallet", joinColumns = {@JoinColumn(name = "id_account")},
            inverseJoinColumns = @JoinColumn(name = "id_wallet"))
    private Wallet wallet;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_account_permission", joinColumns = {@JoinColumn(name = "id_account")},
            inverseJoinColumns = @JoinColumn(name = "id_permission"))
    private List<Permission> permissions = new ArrayList<>();

    public List<String> getPermissionsAccount(){
        return this.permissions.stream().map(permission -> permission.getName()).collect(Collectors.toList());
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.permissions;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
