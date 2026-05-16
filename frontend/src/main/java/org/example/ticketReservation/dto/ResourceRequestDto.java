package org.example.ticketReservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.ticketReservation.domain.ResourceType;

@Data
public class ResourceRequestDto {
    @NotBlank(message = "the resource's name cannot be empty")
    private String name;

    @NotNull(message = "the resource's type cannot be empty")
    private ResourceType type;

    private int capacity;
}
