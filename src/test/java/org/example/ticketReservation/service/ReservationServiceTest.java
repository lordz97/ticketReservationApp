package org.example.ticketReservation.service;

import org.example.ticketReservation.domain.Reservation;
import org.example.ticketReservation.domain.Resource;
import org.example.ticketReservation.domain.ResourceType;
import org.example.ticketReservation.domain.User;
import org.example.ticketReservation.dto.ReservationRequestDto;
import org.example.ticketReservation.exception.InvalidReservationException;
import org.example.ticketReservation.repository.ReservationRepository;
import org.example.ticketReservation.repository.ResourceRepository;
import org.example.ticketReservation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private User testUser;

    private Resource testResource;

    private ReservationRequestDto dto;

    @Test
    public void testDateValidation(){
        testUser = User.builder()
                .name("Lord")
                .email("lord@gmail.com")
                .password("lordzeus")
                .build();

        testResource = Resource.builder()
                .id(1L)
                .name("Conference Room")
                .type(ResourceType.ROOM)
                .capacity(5)
                .build();

        dto = ReservationRequestDto.builder()
                .id(testResource.getId())
                .start(LocalDateTime.now().plusDays(1).withMinute(30))
                .end(LocalDateTime.now().plusDays(1).withMinute(15))
                .build();

        assertThrows(InvalidReservationException.class, ()-> reservationService.createReservation(testUser.getEmail(), dto));
    }

    @Test
    void testReservationCreation(){
        testUser = User.builder()
                .name("Lord")
                .email("lord@gmail.com")
                .password("lordzeus")
                .build();

        testResource = Resource.builder()
                .id(1L)
                .name("Conference Room")
                .type(ResourceType.ROOM)
                .capacity(5)
                .build();

        dto = ReservationRequestDto.builder()
                .id(testResource.getId())
                .start(LocalDateTime.now().plusDays(1).withMinute(15))
                .end(LocalDateTime.now().plusDays(1).withMinute(30))
                .build();

        when(userRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));

        when(resourceRepository.findIdWithLock(testResource.getId()))
                .thenReturn(Optional.of(testResource));

        reservationService.createReservation(testUser.getEmail(), dto);

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void testReservationCancel(){
        testUser = User.builder()
                .name("Lord")
                .email("lord@gmail.com")
                .password("lordzeus")
                .build();

        testResource = Resource.builder()
                .id(1L)
                .name("Conference Room")
                .type(ResourceType.ROOM)
                .capacity(5)
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(testUser)
                .resource(testResource)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusHours(1))
                .build();

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));
    }

}
