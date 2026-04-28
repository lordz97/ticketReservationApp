package org.example.ticketReservation.service;

import lombok.RequiredArgsConstructor;
import org.example.ticketReservation.domain.Resource;
import org.example.ticketReservation.domain.ResourceType;
import org.example.ticketReservation.dto.ResourceRequestDto;
import org.example.ticketReservation.exception.ResourceNotFoundException;
import org.example.ticketReservation.repository.ResourceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public Resource createResource(ResourceRequestDto request){

        Resource resource = Resource.builder()
                .name(request.getName())
                .type(request.getType())
                .capacity(request.getCapacity())
                .active(true)
                .build();

        return resourceRepository.save(resource);
    }

    public Page<Resource> findAllResources(Pageable pageable){
        return resourceRepository.findAll(pageable);
    }

    public Page<Resource> searchByType(ResourceType type, Pageable pageable){
        return resourceRepository.findByTypeAndActiveTrue(type, pageable);
    }

    @Transactional
    public void deactivateResource(Long id){
        Resource resource = resourceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("The provided resource does not exist"));
        resource.setActive(false);
    }
}
