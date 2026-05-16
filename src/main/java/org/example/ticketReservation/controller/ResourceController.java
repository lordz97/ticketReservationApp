package org.example.ticketReservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ticketReservation.domain.Resource;
import org.example.ticketReservation.dto.ResourceRequestDto;
import org.example.ticketReservation.dto.ResourceSearchRequestDto;
import org.example.ticketReservation.service.ResourceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/resources")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Resource> createResource(@Valid @RequestBody ResourceRequestDto dto) {
        return ResponseEntity.ok(resourceService.createResource(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<Resource>> findAllResources(@PageableDefault(size = 10, sort = "type") Pageable pageable) {
        return ResponseEntity.ok(resourceService.findAllResources(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Resource>> searchByType(@RequestParam  ResourceSearchRequestDto dto, @PageableDefault(size = 10, sort = "type") Pageable pageable){
        return ResponseEntity.ok(resourceService.searchByType(dto.getType(), pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateResource(@PathVariable Long id) {
        resourceService.deactivateResource(id);

        return ResponseEntity.noContent().build();
    }
}
