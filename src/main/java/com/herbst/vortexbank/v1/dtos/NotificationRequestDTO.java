package com.herbst.vortexbank.v1.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class NotificationRequestDTO {
    private Long accountId;
    private int page;
    private int size;
}
