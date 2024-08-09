package com.herbst.vortexbank.repositories;

import com.herbst.vortexbank.entities.Account;
import com.herbst.vortexbank.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    Page<Notification> findAllByAccount(Account account, Pageable pageable);
}
