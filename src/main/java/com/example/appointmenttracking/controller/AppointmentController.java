package com.example.appointmenttracking.controller;

import com.example.appointmenttracking.model.*;
import com.example.appointmenttracking.model.AppointmentStatus;
import com.example.appointmenttracking.repository.AppointmentRepository;
import com.example.appointmenttracking.repository.DoctorRepository;
import com.example.appointmenttracking.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestParam Long doctorId,
                                             @RequestParam Long patientId,
                                             @RequestParam String date) {

        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (doctorOpt.isEmpty() || patientOpt.isEmpty()) return ResponseEntity.badRequest().build();

        Doctor doctor = doctorOpt.get();
        Patient patient = patientOpt.get();
        LocalDate appointmentDate = LocalDate.parse(date);

        List<Appointment> appointments = appointmentRepository.findByDoctorAndAppointmentDateOrderBySerialNumber(doctor, appointmentDate);
        int nextSerial = appointments.size() + 1;

        Appointment appointment = Appointment.builder()
                .appointmentDate(appointmentDate)
                .serialNumber(nextSerial)
                .status(AppointmentStatus.WAITING)
                .doctor(doctor)
                .patient(patient)
                .build();

        return ResponseEntity.ok(appointmentRepository.save(appointment));
    }

    @GetMapping("/doctor/{doctorId}/live-status")
    public ResponseEntity<?> getLiveStatus(@PathVariable Long doctorId,
                                           @RequestParam String date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) return ResponseEntity.notFound().build();

        Doctor doctor = doctorOpt.get();
        LocalDate appointmentDate = LocalDate.parse(date);

        List<Appointment> allAppointments = appointmentRepository.findByDoctorAndAppointmentDateOrderBySerialNumber(doctor, appointmentDate);
        long completed = appointmentRepository.countByDoctorAndAppointmentDateAndStatus(doctor, appointmentDate, AppointmentStatus.COMPLETED);
        long remaining = allAppointments.size() - completed;

        Appointment current = allAppointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.WAITING)
                .findFirst().orElse(null);

        Map<String, Object> status = new HashMap<>();
        status.put("doctorName", doctor.getName());
        status.put("doctorAvailable", doctor.isAvailable());
        status.put("currentPatientSerial", current != null ? current.getSerialNumber() : null);
        status.put("patientsRemaining", remaining);
        status.put("remarks", doctor.getRemarks());

        return ResponseEntity.ok(status);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam AppointmentStatus status,
                                          @RequestParam(required = false) String delay) {
        return appointmentRepository.findById(id).map(appointment -> {
            appointment.setStatus(status);
            if (delay != null && delay.equalsIgnoreCase("yes")) {
                appointment.setDelayReported(true);
            }
            appointment.setEndTime(LocalTime.now());
            return ResponseEntity.ok(appointmentRepository.save(appointment));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/shift-back")
    public ResponseEntity<?> shiftBackSerial(@PathVariable Long id, @RequestParam int newSerial) {
        return appointmentRepository.findById(id).map(appointment -> {
            appointment.setSerialNumber(newSerial);
            return ResponseEntity.ok(appointmentRepository.save(appointment));
        }).orElse(ResponseEntity.notFound().build());
    }
}
