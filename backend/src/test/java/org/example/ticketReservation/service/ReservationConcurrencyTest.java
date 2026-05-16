package org.example.ticketReservation.service;

import lombok.extern.slf4j.Slf4j;
import org.example.ticketReservation.domain.Resource;
import org.example.ticketReservation.domain.ResourceType;
import org.example.ticketReservation.domain.User;
import org.example.ticketReservation.dto.ReservationRequestDto;
import org.example.ticketReservation.repository.ReservationRepository;
import org.example.ticketReservation.repository.ResourceRepository;
import org.example.ticketReservation.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
public class ReservationConcurrencyTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ReservationService reservationService;

    private User testUser;

    private Resource testResource;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
        resourceRepository.deleteAll();
        reservationRepository.deleteAll();

        testUser = User.builder()
                .name("John")
                .email("doe@email.com")
                .password("john")
                .build();

        userRepository.save(testUser);

        testResource = Resource.builder()
                .name("Salle de conf")
                .type(ResourceType.ROOM)
                .capacity(10)
                .build();

        resourceRepository.save(testResource);
    }

    @AfterEach
    void tearDown() {
        reservationRepository.deleteAll();
        userRepository.deleteAll();
        resourceRepository.deleteAll();
    }

    @Test
    public void testConcurrency() throws InterruptedException {
        ReservationRequestDto request = ReservationRequestDto.builder()
                .id(testResource.getId())
                .start(LocalDateTime.now().plusDays(1).withHour(12).withMinute(15))
                .end(LocalDateTime.now().plusDays(1).withHour(14).withMinute(15))
                .build();

        int numberOfThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        try {
            for(int i = 0; i < numberOfThreads; i++) {
                int finalI = i;
                executor.execute(()-> {
                    try{
                        startLatch.await();
                        reservationService.createReservation(testUser.getEmail(), request);
                        log.info("thread number {} succeeded", finalI);
                        successCount.incrementAndGet();
                    }catch (Exception e){
                        log.error("thread number {} failed", finalI);
                        failureCount.incrementAndGet();
                    }finally {
                        doneLatch.countDown();
                    }
                });
            }
            startLatch.countDown();
            doneLatch.await();
        } finally {
            executor.shutdown();
            if(!executor.awaitTermination(5, TimeUnit.SECONDS))
                executor.shutdownNow();
        }

        assertEquals(1, successCount.get(), "There must be only 1 test that succeed");
        assertEquals(9, failureCount.get(), "9 tests must fail");

        assertEquals(1, reservationRepository.count(), "there must be exactly one reservation in the DB");
    }
}
