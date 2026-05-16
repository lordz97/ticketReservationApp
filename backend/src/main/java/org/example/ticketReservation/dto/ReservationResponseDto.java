package org.example.ticketReservation.dto;

import lombok.Builder;
import lombok.Data;
import org.example.ticketReservation.domain.ReservationStatus;
import org.example.ticketReservation.domain.ResourceType;

import java.time.LocalDateTime;

@Builder @Data
public class ReservationResponseDto {
    private Long id;

    private String resourceName;

    private LocalDateTime start;

    private LocalDateTime end;

    @Builder.Default
    private ReservationStatus status = ReservationStatus.ACTIVE;

    private ResourceType type;
}
