package com.example.appointmenttracking.controller;

import com.example.appointmenttracking.model.SerialChangeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebSocketNotificationController {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyAdminOfSerialChangeRequest(SerialChangeRequest request) {
        messagingTemplate.convertAndSend("/topic/serial-change-requests", request);
    }

    public void notifyPatientOfApproval(Long patientId, String message) {
        messagingTemplate.convertAndSend("/topic/patient/" + patientId, message);
    }
}

