package com.example.appointmenttracking.repository;

import com.example.appointmenttracking.model.SerialChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SerialChangeRequestRepository extends JpaRepository<SerialChangeRequest, Long> {
}
