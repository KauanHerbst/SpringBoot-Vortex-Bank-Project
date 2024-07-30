package com.herbst.vortexbank.v1.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.herbst.vortexbank.entities.Permission;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonPropertyOrder({"id", "name", "email", "cpf", "dateOfBirth", "telephone", "permissions", "walletKey",
        "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled"})
public class AccountDTO extends RepresentationModel<AccountDTO> {
    @JsonProperty("id")
    private Long accountId;
    private String name;
    private String CPF;
    private String email;
    private String telephone;
    private String dateOfBirth;
    private Date accountCreatedAt;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private Boolean enabled;
    private List<Permission> permissions;
    private String walletKey;
}
