package app.web;

import app.model.Notification;
import app.model.NotificationStatus;
import app.service.NotificationService;
import app.web.DtoMapper.DtoMapper;
import app.web.dto.NotificationRequest;
import app.web.dto.NotificationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void sendNotification_ReturnsCreatedStatus() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        NotificationRequest request = new NotificationRequest();
        request.setUserId(userId);
        request.setUserEmail("test@example.com");
        request.setSubject("Test Subject");
        request.setBody("Test Body");

        LocalDateTime now = LocalDateTime.now();
        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .subject(request.getSubject())
                .body(request.getBody())
                .createdOn(now)
                .status(NotificationStatus.SUCCEEDED)
                .userId(userId)
                .build();

        NotificationResponse expectedResponse = NotificationResponse.builder()
                .createdOn(now)
                .build();

        when(notificationService.sendNotification(any(NotificationRequest.class))).thenReturn(notification);

        // Configure MockMvc
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdOn").exists());
    }

    // Helper method to convert objects to JSON string
    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}