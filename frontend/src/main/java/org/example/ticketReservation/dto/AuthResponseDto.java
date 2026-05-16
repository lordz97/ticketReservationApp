package org.example.ticketReservation.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    private String token;
}
