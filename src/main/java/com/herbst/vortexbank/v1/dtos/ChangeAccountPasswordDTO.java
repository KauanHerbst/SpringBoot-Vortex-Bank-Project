package com.herbst.vortexbank.v1.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChangeAccountPasswordDTO {
    private String name;
    private String email;
    private String CPF;
    private String password;
    private String newPassword;

}
