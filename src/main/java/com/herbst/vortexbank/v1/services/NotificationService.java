package com.herbst.vortexbank.v1.services;

import com.herbst.vortexbank.entities.Account;
import com.herbst.vortexbank.entities.Notification;
import com.herbst.vortexbank.exceptions.EntityNotFoundException;
import com.herbst.vortexbank.mapper.CustomMapperNotification;
import com.herbst.vortexbank.repositories.AccountRepository;
import com.herbst.vortexbank.repositories.NotificationRepository;
import com.herbst.vortexbank.util.NotificationTypeEnum;
import com.herbst.vortexbank.v1.dtos.NotificationDTO;
import com.herbst.vortexbank.v1.dtos.NotificationRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class NotificationService {

    private static Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private SimpMessageHeaderAccessor accessor;

    public void createNotification(String title, String message, NotificationTypeEnum type, Account account){
        Notification notification = new Notification();
        notification.setAccount(account);
        notification.setTitle(title);
        notification.setType(type);
        notification.setIsRead(false);
        notification.setTimestamp(new Date());
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    public void loadAccessor(SimpMessageHeaderAccessor accessor){
        this.accessor = accessor;
    }


    public void upadteReadNotification(String notificationId){
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(EntityNotFoundException::new);
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public Page<NotificationDTO> getNotifications(NotificationRequestDTO dto){
        Account account = accountRepository.findById(dto.getAccountId()).orElseThrow(EntityNotFoundException::new);
        Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize());
        return notificationRepository.findAllByAccount(account, pageable).map(CustomMapperNotification::mapperObject);
    }

    public void notifyAccount(Long accountId){
       Long accountIdAccessor = (Long) accessor.getSessionAttributes().get("accountId");
       if(accountIdAccessor == accountId){
           NotificationRequestDTO notificationRequestDTO = new NotificationRequestDTO();
           notificationRequestDTO.setAccountId(accountId);
           notificationRequestDTO.setPage(0);
           notificationRequestDTO.setSize(20);

           Page<NotificationDTO> notifications = getNotifications(notificationRequestDTO);
           messagingTemplate.convertAndSend("/topic/response", notifications);
       }
    }

}
