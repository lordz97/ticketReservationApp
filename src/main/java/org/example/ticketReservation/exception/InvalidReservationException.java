package org.example.ticketReservation.exception;

public class InvalidReservationException extends RuntimeException{
    public InvalidReservationException(String message){
        super(message);
    }
}
