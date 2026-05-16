package org.example.ticketReservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDto {
    @NotBlank(message = "Name must not be empty")
    private String name;

    @NotBlank(message = "Email must not be empty")
    private String email;

    @NotNull(message = "password must not be empty")
    @Size(min = 8, message = "Password must have at least 8 caracters")
    private String password;
}
