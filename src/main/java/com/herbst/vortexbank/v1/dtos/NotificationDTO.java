package com.herbst.vortexbank.v1.dtos;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class NotificationDTO implements Serializable {
    private String id;
    private Long accountId;
    private String title;
    private String message;
    private Date timestamp;
    private String notificationType;
    private Boolean isRead;
}
