package org.example.ticketReservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ticketReservation.domain.Resource;
import org.example.ticketReservation.dto.ReservationForCalendarDto;
import org.example.ticketReservation.dto.ReservationRequestDto;
import org.example.ticketReservation.dto.ReservationResponseDto;
import org.example.ticketReservation.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/reservation")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService service;

    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation(@Valid @RequestBody ReservationRequestDto dto, Principal principal){
        String email = principal.getName();
        return ResponseEntity.ok(service.createReservation(email, dto));
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponseDto>> getCalendarReservation(@RequestParam Long resourceId,
                                                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime from,
                                                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime to){
        ReservationForCalendarDto dto = ReservationForCalendarDto.builder()
                .id(resourceId)
                .from(from)
                .to(to)
                .build();

        return ResponseEntity.ok(service.getCalendarReservation(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id, Principal principal){
        String email = principal.getName();
        service.cancelReservation(email, id);
        return ResponseEntity.noContent().build();
    }
}
