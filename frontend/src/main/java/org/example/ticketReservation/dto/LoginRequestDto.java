package org.example.ticketReservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotNull(message = "Email must not be empty")
    private String email;
    @NotNull(message = "Password must not be empty")
    private String password;
}
