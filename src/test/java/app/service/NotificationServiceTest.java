package app.service;

import app.model.Notification;
import app.model.NotificationStatus;
import app.repository.NotificationRepository;
import app.web.dto.NotificationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private MailSender mailSender;

    @Mock
    private NotificationRepository notificationRepository;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(mailSender, notificationRepository);
    }

    @Test
    void sendNotification_Success() {

        UUID userId = UUID.randomUUID();
        NotificationRequest request = new NotificationRequest();
        request.setUserId(userId);
        request.setUserEmail("test@example.com");
        request.setSubject("Test Subject");
        request.setBody("Test Body");

        Notification savedNotification = Notification.builder()
                .id(UUID.randomUUID())
                .subject(request.getSubject())
                .body(request.getBody())
                .userId(userId)
                .status(NotificationStatus.SUCCEEDED)
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notificationService.sendNotification(request);

        assertNotNull(result);
        assertEquals(NotificationStatus.SUCCEEDED, result.getStatus());

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("test@example.com", sentMessage.getTo()[0]);
        assertEquals("Test Subject", sentMessage.getSubject());
        assertEquals("Test Body", sentMessage.getText());

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void sendNotification_FailureToSendEmail() {

        UUID userId = UUID.randomUUID();
        NotificationRequest request = new NotificationRequest();
        request.setUserId(userId);
        request.setUserEmail("test@example.com");
        request.setSubject("Test Subject");
        request.setBody("Test Body");

        Notification savedNotification = Notification.builder()
                .id(UUID.randomUUID())
                .subject(request.getSubject())
                .body(request.getBody())
                .userId(userId)
                .status(NotificationStatus.FAILED)
                .build();

        doThrow(new RuntimeException("Failed to send email")).when(mailSender).send(any(SimpleMailMessage.class));
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notificationService.sendNotification(request);

        assertNotNull(result);
        assertEquals(NotificationStatus.FAILED, result.getStatus());

        verify(notificationRepository).save(any(Notification.class));
    }
}