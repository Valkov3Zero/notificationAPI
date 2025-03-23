package app.web;

import app.model.Notification;
import app.service.NotificationService;
import app.web.DtoMapper.DtoMapper;
import app.web.dto.NotificationRequest;
import app.web.dto.NotificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody NotificationRequest request) {

        Notification notification = notificationService.sendNotification(request);

        NotificationResponse notificationResponse = DtoMapper.fromNotification(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(notificationResponse);
    }
}
