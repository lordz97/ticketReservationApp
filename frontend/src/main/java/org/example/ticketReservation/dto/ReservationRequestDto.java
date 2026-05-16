package org.example.ticketReservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class ReservationRequestDto {
    @NotNull(message = "Resource Id cannot be empty")
    private Long id;

    @NotNull(message = "Start time cannot be empty")
    private LocalDateTime start;

    @NotNull(message = "End time cannot be empty")
    private LocalDateTime end;
}
