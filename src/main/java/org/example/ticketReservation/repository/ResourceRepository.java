package org.example.ticketReservation.repository;

import org.example.ticketReservation.domain.Resource;
import org.example.ticketReservation.domain.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource,Long>{
     Page<Resource> findByTypeAndActiveTrue(ResourceType resourceType, Pageable pageable);
}
