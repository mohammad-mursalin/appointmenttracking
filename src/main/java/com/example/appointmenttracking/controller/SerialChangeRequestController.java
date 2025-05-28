package com.example.appointmenttracking.controller;

import com.example.appointmenttracking.dto.SerialChangeRequestDTO;
import com.example.appointmenttracking.model.Appointment;
import com.example.appointmenttracking.model.ChangeRequestStatus;
import com.example.appointmenttracking.model.SerialChangeRequest;
import com.example.appointmenttracking.repository.AppointmentRepository;
import com.example.appointmenttracking.repository.SerialChangeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/serial-change")
@RequiredArgsConstructor
public class SerialChangeRequestController {

    private final SerialChangeRequestRepository requestRepository;
    private final AppointmentRepository appointmentRepository;
    private final WebSocketNotificationController wsController;

    @PostMapping("/request")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<?> requestChange(@RequestBody SerialChangeRequestDTO dto) {
        Optional<Appointment> apptOpt = appointmentRepository.findById(dto.getAppointmentId());
        if (apptOpt.isEmpty()) return ResponseEntity.badRequest().body("Appointment not found");

        SerialChangeRequest request = SerialChangeRequest.builder()
                .appointment(apptOpt.get())
                .requestedSerialNumber(dto.getRequestedSerial())
                .status(ChangeRequestStatus.PENDING)
                .build();

        SerialChangeRequest saved = requestRepository.save(request);
        wsController.notifyAdminOfSerialChangeRequest(saved);

        return ResponseEntity.ok(saved);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveRequest(@PathVariable Long id) {
        return requestRepository.findById(id).map(req -> {
            Appointment appt = req.getAppointment();
            appt.setSerialNumber(req.getRequestedSerialNumber());
            req.setStatus(ChangeRequestStatus.APPROVED);

            appointmentRepository.save(appt);
            requestRepository.save(req);

            wsController.notifyPatientOfApproval(appt.getPatient().getId(), "Your serial change has been approved.");

            return ResponseEntity.ok(req);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id) {
        return requestRepository.findById(id).map(req -> {
            req.setStatus(ChangeRequestStatus.REJECTED);
            requestRepository.save(req);

            wsController.notifyPatientOfApproval(req.getAppointment().getPatient().getId(), "Your serial change request was rejected.");

            return ResponseEntity.ok(req);
        }).orElse(ResponseEntity.notFound().build());
    }
}

