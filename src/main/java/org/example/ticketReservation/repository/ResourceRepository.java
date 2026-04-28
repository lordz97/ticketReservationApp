package org.example.ticketReservation.repository;

import jakarta.persistence.LockModeType;
import org.example.ticketReservation.domain.Resource;
import org.example.ticketReservation.domain.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource,Long>{
     Page<Resource> findByTypeAndActiveTrue(ResourceType resourceType, Pageable pageable);

     @Query("SELECT r FROM Resource r WHERE r.id=:id")
     @Lock(LockModeType.PESSIMISTIC_WRITE)
     Optional<Resource> findIdWithLock(@Param("id") Long id);
}
