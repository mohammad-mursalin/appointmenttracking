package com.example.appointmenttracking.dto;

import com.example.appointmenttracking.model.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private Role role; // Optional, default to USER if not provided
}
