package org.example.ticketReservation.service;

import lombok.RequiredArgsConstructor;
import org.example.ticketReservation.domain.Reservation;
import org.example.ticketReservation.domain.ReservationStatus;
import org.example.ticketReservation.domain.Resource;
import org.example.ticketReservation.domain.User;
import org.example.ticketReservation.dto.ReservationForCalendarDto;
import org.example.ticketReservation.dto.ReservationRequestDto;
import org.example.ticketReservation.dto.ReservationResponseDto;
import org.example.ticketReservation.exception.InvalidReservationException;
import org.example.ticketReservation.exception.ReservationNotFoundException;
import org.example.ticketReservation.exception.ResourceNotAvailableException;
import org.example.ticketReservation.exception.ResourceNotFoundException;
import org.example.ticketReservation.repository.ReservationRepository;
import org.example.ticketReservation.repository.ResourceRepository;
import org.example.ticketReservation.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;

    @Transactional
    public ReservationResponseDto createReservation(String email, ReservationRequestDto dto){
        LocalDateTime end = dto.getEnd();
        LocalDateTime start = dto.getStart();
        Long resourceId = dto.getId();
        if(end.isBefore(start) || end.isEqual(start) || start.isBefore(LocalDateTime.now())){
            throw new InvalidReservationException("Invalid reservation parameter");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User does not exist"));

        Resource resource = resourceRepository.findIdWithLock(resourceId).orElseThrow(()-> new ResourceNotFoundException("Resource does not exist"));

        if(!resource.getActive()){
            throw new ResourceNotAvailableException("Resource not available");
        }

        if(reservationRepository.hasOverlappingReservations(resourceId, start, end)){
            throw new InvalidReservationException("Reservation for this resource already exists");
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .resource(resource)
                .startDateTime(start)
                .endDateTime(end)
                .build();

        reservationRepository.save(reservation);

        return ReservationResponseDto.builder()
                .id(reservation.getId())
                .resourceName(resource.getName())
                .type(resource.getType())
                .status(ReservationStatus.ACTIVE)
                .start(start)
                .end(end)
                .build();
    }

    public List<ReservationResponseDto> getCalendarReservation(ReservationForCalendarDto dto){
        List<Reservation> reservations = reservationRepository.findReservationsForCalendar(dto.getId(), dto.getFrom(), dto.getTo());

        return reservations.stream().map(reservation -> ReservationResponseDto.builder()
                .id(reservation.getId())
                .status(reservation.getStatus())
                .resourceName(reservation.getResource().getName())
                .start(reservation.getStartDateTime())
                .end(reservation.getEndDateTime())
                .type(reservation.getResource().getType())
                .build())
                .toList();
    }

    @Transactional
    public void cancelReservation(String email, Long id){
        Reservation reservation = reservationRepository.findById(id).orElseThrow(()-> new ReservationNotFoundException("Reservation does not exist"));

        if(reservation.getStartDateTime().isBefore(LocalDateTime.now()))
            throw new InvalidReservationException("Impossible to cancel a current or past reservation");

        if(email.equals(reservation.getUser().getEmail())){
            reservation.setStatus(ReservationStatus.CANCELLED);
        }
        else
            throw new ReservationNotFoundException("Access Denied");
    }
}
