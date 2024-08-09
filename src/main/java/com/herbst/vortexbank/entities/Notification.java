package com.herbst.vortexbank.entities;

import com.herbst.vortexbank.util.NotificationTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;

@Entity
@Table(name = "tb_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Notification {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(length = 36)
    private String id;
    private String title;
    private String message;
    private Date timestamp;
    @Column(name = "notification_type")
    private String notificationType;
    @Column(name = "is_read")
    private Boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    public void setType(NotificationTypeEnum typeEnum){
        this.notificationType = typeEnum.getValue();
    }
}
