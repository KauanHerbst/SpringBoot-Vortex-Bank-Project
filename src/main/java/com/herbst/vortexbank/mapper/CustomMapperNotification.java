package com.herbst.vortexbank.mapper;

import com.herbst.vortexbank.entities.Notification;
import com.herbst.vortexbank.v1.dtos.NotificationDTO;

public class CustomMapperNotification {

    public static NotificationDTO mapperObject(Notification notification){
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setAccountId(notification.getAccount().getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setTimestamp(notification.getTimestamp());
        dto.setNotificationType(notification.getNotificationType());
        dto.setIsRead(notification.getIsRead());
        return dto;
    }
}
