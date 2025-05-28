package com.example.appointmenttracking.service;

import com.example.appointmenttracking.model.Patient;
import com.example.appointmenttracking.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PatientRepository patientRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(patient.getEmail())
                .password(patient.getPassword())
                .roles(patient.getRole().name().replace("ROLE_", ""))
                .build();
    }
}
