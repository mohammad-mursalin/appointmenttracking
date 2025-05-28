package com.example.appointmenttracking.controller;

import com.example.appointmenttracking.model.Doctor;
import com.example.appointmenttracking.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorRepository doctorRepository;

    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        return doctorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Doctor> addDoctor(@RequestBody Doctor doctor) {
        Doctor saved = doctorRepository.save(doctor);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateAvailability(@PathVariable Long id, @RequestParam boolean available, @RequestParam(required = false) String remarks) {
        return doctorRepository.findById(id).map(doctor -> {
            doctor.setAvailable(available);
            if (remarks != null) doctor.setRemarks(remarks);
            return ResponseEntity.ok(doctorRepository.save(doctor));
        }).orElse(ResponseEntity.notFound().build());
    }
}
