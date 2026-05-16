package org.example.ticketReservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.ticketReservation.domain.ResourceType;

@Data
public class ResourceSearchRequestDto {
    @NotNull(message = "Resource's type cannot be empty")
    private ResourceType type;
}
