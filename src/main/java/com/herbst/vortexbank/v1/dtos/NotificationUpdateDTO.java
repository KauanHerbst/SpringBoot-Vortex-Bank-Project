package com.herbst.vortexbank.v1.dtos;

import jakarta.persistence.Entity;
import lombok.*;

import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class NotificationUpdateDTO implements Serializable {
    private String notificationId;
}
