package org.example.ticketReservation.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.ticketReservation.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExistsException(UserAlreadyExistsException e) {

        log.error("User already exists error: {}", e.getMessage());

        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .error(e.getMessage())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
        .build();

        return ResponseEntity.badRequest().body(errorResponseDto);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException (ResourceNotFoundException e) {

        log.error("Resource not found error: {}", e.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .error(e.getMessage())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleReservationNotFoundException(ReservationNotFoundException e){
        log.error("Reservation not found error: {}", e.getMessage());

        ErrorResponseDto dto = ErrorResponseDto.builder()
                .error(e.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
        .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dto);
    }
}
