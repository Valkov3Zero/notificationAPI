package app.web.DtoMapper;

import app.model.Notification;
import app.web.dto.NotificationResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static NotificationResponse fromNotification(Notification notification) {


        return NotificationResponse.builder()
                .createdOn(notification.getCreatedOn())
                .build();
    }
}
