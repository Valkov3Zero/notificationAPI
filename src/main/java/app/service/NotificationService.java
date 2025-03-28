package app.service;

import app.model.Notification;
import app.model.NotificationStatus;
import app.repository.NotificationRepository;
import app.web.dto.NotificationRequest;
import app.web.dto.NotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class NotificationService {

    private final MailSender mailSender;
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(MailSender mailSender, NotificationRepository notificationRepository) {
        this.mailSender = mailSender;
        this.notificationRepository = notificationRepository;
    }


    public Notification sendNotification(NotificationRequest request) {

        UUID userId = request.getUserId();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getUserEmail());
        message.setSubject(request.getSubject());
        message.setText(request.getBody());

        Notification notification = Notification.builder()
                .subject(request.getSubject())
                .body(request.getBody())
                .createdOn(LocalDateTime.now())
                .userId(userId)
                .build();

        try{
            mailSender.send(message);
            notification.setStatus(NotificationStatus.SUCCEEDED);
        }catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            log.warn("There was an issue sending the email to %s due to %s".formatted(request.getUserEmail(), e.getMessage()));
        }

        return notificationRepository.save(notification);
    }


    public NotificationResponse getNotificationByUser(UUID userId) {
        return notificationRepository.findByUserId(userId);
    }
}
