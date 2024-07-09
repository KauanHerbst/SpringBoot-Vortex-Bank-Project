package com.herbst.vortexbank.v1.dtos;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TokenDTO implements Serializable {
    private String name;
    private Date created;
    private Date expiration;
    private Boolean authenticated;
    private String accessToken;
    private String refreshToken;

}
