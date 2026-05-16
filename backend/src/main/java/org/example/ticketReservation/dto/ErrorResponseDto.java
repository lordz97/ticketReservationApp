package org.example.ticketReservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class ErrorResponseDto {
    private String error;

    private LocalDateTime timestamp;

    private int status;
}
