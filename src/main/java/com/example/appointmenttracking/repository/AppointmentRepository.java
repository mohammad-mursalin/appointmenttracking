package com.example.appointmenttracking.repository;

import com.example.appointmenttracking.model.Appointment;
import com.example.appointmenttracking.model.AppointmentStatus;
import com.example.appointmenttracking.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorAndAppointmentDateOrderBySerialNumber(Doctor doctor, LocalDate date);
    long countByDoctorAndAppointmentDateAndStatus(Doctor doctor, LocalDate date, AppointmentStatus status);
    Appointment findByDoctorAndAppointmentDateAndSerialNumber(Doctor doctor, LocalDate date, Integer serial);
}
