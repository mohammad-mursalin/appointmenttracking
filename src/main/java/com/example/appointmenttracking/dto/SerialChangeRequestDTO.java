package com.example.appointmenttracking.dto;

import lombok.Data;

@Data
public class SerialChangeRequestDTO {

    private Long appointmentId;
    private int requestedSerial;
}
