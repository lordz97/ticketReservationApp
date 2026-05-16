package org.example.ticketReservation.exception;

public class ReservationNotFoundException extends RuntimeException{
    public ReservationNotFoundException(String message){
        super(message);
    }
}
