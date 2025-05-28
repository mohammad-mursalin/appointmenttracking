package com.example.appointmenttracking.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String specialization;

    private boolean isAvailable; // shows if doctor is sitting now

    private String remarks; // “Doctor will arrive at 10:30 AM”, etc.
}
