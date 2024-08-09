package com.herbst.vortexbank.v1.controllers;

import com.herbst.vortexbank.v1.dtos.NotificationDTO;
import com.herbst.vortexbank.v1.dtos.NotificationRequestDTO;
import com.herbst.vortexbank.v1.dtos.NotificationUpdateDTO;
import com.herbst.vortexbank.v1.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/notifications")
    @SendTo("/topic/response")
    public Page<NotificationDTO> sendAllNotifications(@Payload NotificationRequestDTO request, SimpMessageHeaderAccessor accessor){
        accessor.getSessionAttributes().put("accountId", request.getAccountId());
        notificationService.loadAccessor(accessor);
        return notificationService.getNotifications(request);
    }

    @MessageMapping("/notifications/updates")
    public void udpateNotificationRead(@Payload NotificationUpdateDTO notificationUpdateDTO){
        System.out.println("notification id received: " + notificationUpdateDTO.getNotificationId());
        notificationService.upadteReadNotification(notificationUpdateDTO.getNotificationId());
    }

}
