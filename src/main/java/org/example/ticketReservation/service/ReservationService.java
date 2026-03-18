package org.example.ticketReservation.service;

import lombok.RequiredArgsConstructor;
import org.example.ticketReservation.domain.Reservation;
import org.example.ticketReservation.domain.Resource;
import org.example.ticketReservation.domain.User;
import org.example.ticketReservation.exception.InvalidReservationException;
import org.example.ticketReservation.exception.ResourceNotAvailableException;
import org.example.ticketReservation.repository.ReservationRepository;
import org.example.ticketReservation.repository.ResourceRepository;
import org.example.ticketReservation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;

    @Transactional
    public Reservation createReservation(Long userId, Long resourceId, LocalDateTime start, LocalDateTime end){
        if(start.isBefore(LocalDateTime.now()))
            throw new InvalidReservationException("The start date must be in the future.");
        if(start.isAfter(end) || start.isEqual(end))
            throw new InvalidReservationException("The start date must be before the end date.");

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("User not found"));

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(()-> new IllegalArgumentException("Resource not found"));

        if(!resource.getActive())
            throw new ResourceNotAvailableException("The chosen resource is currently not available");

        boolean hasConflict = reservationRepository.hasOverlappingReservations(resourceId, start, end);
        if(hasConflict)
            throw new ResourceNotAvailableException("The resource is already reserved for that period.");

        Reservation reservation = Reservation.builder()
                .user(user)
                .resource(resource)
                .startDateTime(start)
                .endDateTime(end)
                .build();

        return reservationRepository.save(reservation);
    }
}
