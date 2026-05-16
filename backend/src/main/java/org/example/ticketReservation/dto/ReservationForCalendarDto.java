package org.example.ticketReservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class ReservationForCalendarDto {
    @NotNull(message = "Reservation Id must not be empty")
    private Long id;

    @NotNull(message = "Enter a valid start time")
    private LocalDateTime from;

    @NotNull(message = "Enter a valid end time")
    private LocalDateTime to;
}
