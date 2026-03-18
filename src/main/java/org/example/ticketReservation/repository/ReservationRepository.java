package org.example.ticketReservation.repository;

import org.example.ticketReservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT  case when count(r) > 0 then true else false end from Reservation r " +
            "where r.resource.id = :resourceId " +
            "AND r.status = 'ACTIVE'" +
            "AND r.startDateTime < :newEnd " +
            "AND r.endDateTime > :newStart")
    boolean hasOverlappingReservations(@Param("resourceId") Long resourceId,
                                       @Param("newStart") LocalDateTime newStart,
                                       @Param("newEnd") LocalDateTime newEnd);
}
