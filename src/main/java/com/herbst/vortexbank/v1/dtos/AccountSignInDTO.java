package com.herbst.vortexbank.v1.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AccountSignInDTO implements Serializable {
    @NotBlank(message = "CPF cannot be Blank/Null")
    @Size(min = 11, max = 11, message = "CPF has 11 digits")
    private String CPF;
    @NotBlank(message = "Password cannot be Blank/Null")
    private String password;
}
